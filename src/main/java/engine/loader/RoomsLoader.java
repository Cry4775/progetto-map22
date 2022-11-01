package engine.loader;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import component.entity.AbstractEntity;
import component.entity.container.AbstractContainer;
import component.room.AbstractRoom;
import component.room.CutsceneRoom;
import component.room.MutableRoom;
import component.room.PlayableRoom;
import engine.GameManager;
import engine.database.DBManager;
import engine.loader.json.TypeAdapterHolder;
import engine.loader.json.TypeAdapterHolder.AdapterType;

/**
 * RoomsLoader
 */
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

            Multimap<String, AbstractEntity> objects = mapAllObjects();

            for (AbstractEntity obj : objects.values()) {
                obj.processReferences(objects, rooms);
            }

            GameManager.setCurrentRoom(rooms.get(0));
        } else {
            rooms.addAll(DBManager.load());

            Multimap<String, AbstractEntity> objects = mapAllObjects();

            for (AbstractEntity obj : GameManager.getFullInventory()) {
                objects.put(obj.getId(), obj);
            }

            for (AbstractEntity obj : objects.values()) {
                obj.processReferences(objects, rooms);
            }

            String currentRoomId = DBManager.getCurrentRoomId();

            for (AbstractRoom room : rooms) {
                if (room.getId().equals(currentRoomId)) {
                    GameManager.setCurrentRoom(room);
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

    public static List<AbstractRoom> listAllRooms() {
        List<AbstractRoom> result = new ArrayList<>();

        for (AbstractRoom room : rooms) {
            result.add(room);
            if (room instanceof MutableRoom) {
                MutableRoom mRoom = (MutableRoom) room;

                result.addAll(mRoom.getAllRooms());
            }
        }

        return result;
    }

    public static List<AbstractRoom> listAllRooms(List<AbstractRoom> rooms) {
        List<AbstractRoom> result = new ArrayList<>();

        for (AbstractRoom room : rooms) {
            result.add(room);
            if (room instanceof MutableRoom) {
                MutableRoom mRoom = (MutableRoom) room;

                result.addAll(mRoom.getAllRooms());
            }
        }

        return result;
    }

    public static Multimap<String, AbstractEntity> mapAllObjects() {

        Multimap<String, AbstractEntity> objects = ArrayListMultimap.create();
        Multimap<String, AbstractEntity> result = ArrayListMultimap.create();

        for (AbstractRoom room : listAllRooms()) {
            if (room instanceof PlayableRoom) {
                PlayableRoom pRoom = (PlayableRoom) room;

                if (pRoom.getObjects() != null) {
                    for (AbstractEntity obj : pRoom.getObjects()) {
                        objects.put(obj.getId(), obj);
                    }
                }
            }
        }

        result.putAll(objects);

        for (AbstractEntity obj : objects.values()) {
            if (obj instanceof AbstractContainer) {
                AbstractContainer container = (AbstractContainer) obj;

                for (AbstractEntity cObj : container.getAllObjects()) {
                    result.put(cObj.getId(), cObj);
                }
            }
        }

        return result;
    }

    public static Multimap<String, AbstractEntity> mapAllObjects(List<AbstractRoom> rooms) {

        Multimap<String, AbstractEntity> objects = ArrayListMultimap.create();
        Multimap<String, AbstractEntity> result = ArrayListMultimap.create();

        for (AbstractRoom room : listAllRooms(rooms)) {
            if (room instanceof PlayableRoom) {
                PlayableRoom pRoom = (PlayableRoom) room;

                if (pRoom.getObjects() != null) {
                    for (AbstractEntity obj : pRoom.getObjects()) {
                        objects.put(obj.getId(), obj);
                    }
                }
            }
        }

        result.putAll(objects);

        for (AbstractEntity obj : objects.values()) {
            if (obj instanceof AbstractContainer) {
                AbstractContainer container = (AbstractContainer) obj;

                for (AbstractEntity cObj : container.getAllObjects()) {
                    result.put(cObj.getId(), cObj);
                }
            }
        }

        return result;
    }

    private void linkRooms() {
        List<AbstractRoom> allRooms = listAllRooms();

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
