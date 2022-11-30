package engine.loader;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import com.google.common.collect.Multimap;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import component.entity.AbstractEntity;
import component.room.AbstractRoom;
import component.room.CutsceneRoom;
import component.room.PlayableRoom;
import component.room.Rooms;
import engine.GameManager;
import engine.database.DBManager;
import engine.loader.json.LoaderTypeAdapterHolder;
import engine.loader.json.LoaderTypeAdapterHolder.AdapterType;

public class RoomsLoader implements Runnable {

    private GameManager gameManager;
    private boolean exceptionThrown = false;
    private Mode mode;

    public enum Mode {
        JSON,
        DB
    }

    private List<Thread> workers = new ArrayList<>();

    public boolean isExceptionThrown() {
        return exceptionThrown;
    }

    public RoomsLoader(GameManager gameManager, Mode mode) {
        this.gameManager = gameManager;
        this.mode = mode;
    }

    @Override
    public void run() {
        if (mode == Mode.JSON) {
            Gson gson = new GsonBuilder().setExclusionStrategies(new ExclusionStrategy() {
                @Override
                public boolean shouldSkipField(FieldAttributes f) {
                    // For performance
                    return AbstractRoom.class.equals(f.getDeclaredClass())
                            || AbstractEntity.class.equals(f.getDeclaredClass());
                }

                @Override
                public boolean shouldSkipClass(Class<?> clazz) {
                    // Never
                    return false;
                }
            })
                    .registerTypeAdapterFactory(LoaderTypeAdapterHolder.get(AdapterType.ENTITIES))
                    .registerTypeAdapterFactory(LoaderTypeAdapterHolder.get(AdapterType.ROOMS))
                    .registerTypeAdapterFactory(LoaderTypeAdapterHolder.get(AdapterType.EVENTS))
                    .create();
            Type roomsType = new TypeToken<List<AbstractRoom>>() {}.getType();

            try (BufferedReader in = new BufferedReader(new InputStreamReader(
                    new FileInputStream("resources/rooms.json"), StandardCharsets.UTF_8))) {
                gameManager.addRooms(gson.fromJson(in, roomsType));

            } catch (Exception e) {
                exceptionThrown = true;
                throw new Error(e);
            }

            Multimap<String, AbstractEntity> objects = gameManager.mapAllRoomsObjects();

            for (AbstractEntity obj : objects.values()) {
                obj.processReferences(objects, gameManager.getRooms());
            }

            gameManager.setCurrentRoom(gameManager.getRooms().get(0));
        } else {
            gameManager.addRooms(DBManager.load(gameManager.getInventory()));

            Multimap<String, AbstractEntity> objects = gameManager.mapAllRoomsObjects();
            objects.putAll(gameManager.getInventory().mapAllObjects());

            for (AbstractEntity obj : objects.values()) {
                obj.processReferences(objects, gameManager.getRooms());
            }

            String currentRoomId = DBManager.getCurrentRoomId();
            String previousRoomId = DBManager.getPreviousRoomId();

            for (AbstractRoom room : gameManager.getRooms()) {
                if (room.getId().equals(currentRoomId)) {
                    gameManager.setCurrentRoom(room);
                } else if (room.getId().equals(previousRoomId)) {
                    gameManager.setPreviousRoom(room);
                }

                if (previousRoomId == null && gameManager.getCurrentRoom() != null) {
                    break;
                } else if (gameManager.getPreviousRoom() != null && gameManager.getCurrentRoom() != null) {
                    break;
                }
            }
        }

        linkRooms();

        if (gameManager.getCurrentRoom() instanceof PlayableRoom) {
            PlayableRoom currentRoom = (PlayableRoom) gameManager.getCurrentRoom();
            currentRoom.processRoomLighting(gameManager.getInventory().getObjects());
        }
    }

    private void linkRooms() {
        List<AbstractRoom> allRooms = gameManager.listAllRooms();

        Thread east = new Thread(
                () -> Rooms.loadDirections(allRooms, PlayableRoom::getEastId, PlayableRoom::setEast,
                        PlayableRoom.class));
        Thread west = new Thread(
                () -> Rooms.loadDirections(allRooms, PlayableRoom::getWestId, PlayableRoom::setWest,
                        PlayableRoom.class));
        Thread north = new Thread(
                () -> Rooms.loadDirections(allRooms, PlayableRoom::getNorthId, PlayableRoom::setNorth,
                        PlayableRoom.class));
        Thread south = new Thread(
                () -> Rooms.loadDirections(allRooms, PlayableRoom::getSouthId, PlayableRoom::setSouth,
                        PlayableRoom.class));
        Thread northEast = new Thread(
                () -> Rooms.loadDirections(allRooms, PlayableRoom::getNorthEastId, PlayableRoom::setNorthEast,
                        PlayableRoom.class));
        Thread northWest = new Thread(
                () -> Rooms.loadDirections(allRooms, PlayableRoom::getNorthWestId, PlayableRoom::setNorthWest,
                        PlayableRoom.class));
        Thread southEast = new Thread(
                () -> Rooms.loadDirections(allRooms, PlayableRoom::getSouthEastId, PlayableRoom::setSouthEast,
                        PlayableRoom.class));
        Thread southWest = new Thread(
                () -> Rooms.loadDirections(allRooms, PlayableRoom::getSouthWestId, PlayableRoom::setSouthWest,
                        PlayableRoom.class));
        Thread up = new Thread(
                () -> Rooms.loadDirections(allRooms, PlayableRoom::getUpId, PlayableRoom::setUp,
                        PlayableRoom.class));
        Thread down = new Thread(
                () -> Rooms.loadDirections(allRooms, PlayableRoom::getDownId, PlayableRoom::setDown,
                        PlayableRoom.class));

        Thread nextRoom = new Thread(
                () -> Rooms.loadDirections(allRooms, CutsceneRoom::getNextRoomId, CutsceneRoom::setNextRoom,
                        CutsceneRoom.class));

        workers.add(east);
        workers.add(west);
        workers.add(north);
        workers.add(south);
        workers.add(northEast);
        workers.add(northWest);
        workers.add(southEast);
        workers.add(southWest);
        workers.add(up);
        workers.add(down);
        workers.add(nextRoom);

        for (Thread worker : workers) {
            worker.start();
        }

        for (Thread worker : workers) {
            try {
                worker.join();
            } catch (InterruptedException e) {
                exceptionThrown = true;
                e.printStackTrace();
                throw new Error(e);
            }
        }
    }

}
