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
import engine.GameManager;
import engine.database.DBManager;
import engine.loader.json.TypeAdapterHolder;
import engine.loader.json.TypeAdapterHolder.AdapterType;

public class RoomsLoader implements Runnable {

    private static List<AbstractRoom> rooms;
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

    public RoomsLoader(List<AbstractRoom> rooms, Mode mode) {
        RoomsLoader.rooms = rooms;
        this.mode = mode;
    }

    @Override
    public void run() {
        if (mode == Mode.JSON) {
            Gson gson = new GsonBuilder()
                    .setExclusionStrategies(new ExclusionStrategy() {
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
                    .registerTypeAdapterFactory(TypeAdapterHolder.get(AdapterType.ENTITIES))
                    .registerTypeAdapterFactory(TypeAdapterHolder.get(AdapterType.ROOMS))
                    .registerTypeAdapterFactory(TypeAdapterHolder.get(AdapterType.EVENTS))
                    .create();
            Type roomsType = new TypeToken<List<AbstractRoom>>() {}.getType();

            try (BufferedReader in = new BufferedReader(new InputStreamReader(
                    new FileInputStream("resources/rooms.json"), StandardCharsets.UTF_8))) {
                rooms.addAll(gson.fromJson(in, roomsType));

            } catch (Exception e) {
                exceptionThrown = true;
                throw new Error(e);
            }

            Multimap<String, AbstractEntity> objects = GameManager.mapAllRoomsObjects();

            for (AbstractEntity obj : objects.values()) {
                obj.processReferences(objects, rooms);
            }

            GameManager.setCurrentRoom(rooms.get(0));
        } else {
            rooms.addAll(DBManager.load());

            Multimap<String, AbstractEntity> objects = GameManager.mapAllRoomsObjects();
            objects.putAll(GameManager.mapAllInventoryObjects());

            for (AbstractEntity obj : objects.values()) {
                obj.processReferences(objects, rooms);
            }

            String currentRoomId = DBManager.getCurrentRoomId();
            String previousRoomId = DBManager.getPreviousRoomId();

            for (AbstractRoom room : rooms) {
                if (room.getId().equals(currentRoomId)) {
                    GameManager.setCurrentRoom(room);
                } else if (room.getId().equals(previousRoomId)) {
                    GameManager.setPreviousRoom(room);
                }

                if (previousRoomId == null && GameManager.getCurrentRoom() != null) {
                    break;
                } else if (GameManager.getPreviousRoom() != null
                        && GameManager.getCurrentRoom() != null) {
                    break;
                }
            }
        }

        linkRooms();

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

    private void linkRooms() {
        List<AbstractRoom> allRooms = GameManager.listAllRooms();

        Thread east = new Thread(new RoomsDirectionSetter<>(allRooms, PlayableRoom::getEastId,
                PlayableRoom::setEast, PlayableRoom.class));
        Thread west = new Thread(
                new RoomsDirectionSetter<>(allRooms, PlayableRoom::getWestId, PlayableRoom::setWest,
                        PlayableRoom.class));
        Thread north = new Thread(new RoomsDirectionSetter<>(allRooms, PlayableRoom::getNorthId,
                PlayableRoom::setNorth,
                PlayableRoom.class));
        Thread south = new Thread(new RoomsDirectionSetter<>(allRooms, PlayableRoom::getSouthId,
                PlayableRoom::setSouth,
                PlayableRoom.class));
        Thread northEast = new Thread(new RoomsDirectionSetter<>(allRooms,
                PlayableRoom::getNorthEastId, PlayableRoom::setNorthEast,
                PlayableRoom.class));
        Thread northWest = new Thread(new RoomsDirectionSetter<>(allRooms,
                PlayableRoom::getNorthWestId, PlayableRoom::setNorthWest,
                PlayableRoom.class));
        Thread southEast = new Thread(new RoomsDirectionSetter<>(allRooms,
                PlayableRoom::getSouthEastId, PlayableRoom::setSouthEast,
                PlayableRoom.class));
        Thread southWest = new Thread(new RoomsDirectionSetter<>(allRooms,
                PlayableRoom::getSouthWestId, PlayableRoom::setSouthWest,
                PlayableRoom.class));
        Thread up = new Thread(
                new RoomsDirectionSetter<>(allRooms, PlayableRoom::getUpId, PlayableRoom::setUp,
                        PlayableRoom.class));
        Thread down = new Thread(
                new RoomsDirectionSetter<>(allRooms, PlayableRoom::getDownId, PlayableRoom::setDown,
                        PlayableRoom.class));

        Thread nextRoom = new Thread(new RoomsDirectionSetter<>(allRooms,
                CutsceneRoom::getNextRoomId, CutsceneRoom::setNextRoom,
                CutsceneRoom.class));

        east.start();
        west.start();
        north.start();
        south.start();
        northEast.start();
        northWest.start();
        southEast.start();
        southWest.start();
        up.start();
        down.start();
        nextRoom.start();

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

    }

}
