package di.uniba.map.b.adventure;

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
import di.uniba.map.b.adventure.entities.AbstractEntity;
import di.uniba.map.b.adventure.entities.container.AbstractContainer;
import di.uniba.map.b.adventure.entities.container.ChestlikeContainer;
import di.uniba.map.b.adventure.entities.container.BasicContainer;
import di.uniba.map.b.adventure.entities.container.SocketlikeContainer;
import di.uniba.map.b.adventure.entities.container.pickupable.WearableContainer;
import di.uniba.map.b.adventure.entities.doorlike.UnopenableDoor;
import di.uniba.map.b.adventure.entities.doorlike.Door;
import di.uniba.map.b.adventure.entities.doorlike.InvisibleWall;
import di.uniba.map.b.adventure.entities.humanoid.Human;
import di.uniba.map.b.adventure.entities.object.FireObject;
import di.uniba.map.b.adventure.entities.object.BasicObject;
import di.uniba.map.b.adventure.entities.object.MovableObject;
import di.uniba.map.b.adventure.entities.object.PullableObject;
import di.uniba.map.b.adventure.entities.object.PushableObject;
import di.uniba.map.b.adventure.entities.pickupable.FillableItem;
import di.uniba.map.b.adventure.entities.pickupable.FluidItem;
import di.uniba.map.b.adventure.entities.pickupable.BasicItem;
import di.uniba.map.b.adventure.entities.pickupable.LightSourceItem;
import di.uniba.map.b.adventure.entities.pickupable.ReadableItem;
import di.uniba.map.b.adventure.entities.pickupable.WearableItem;
import di.uniba.map.b.adventure.type.AbstractRoom;
import di.uniba.map.b.adventure.type.AdvEvent;
import di.uniba.map.b.adventure.type.CutsceneRoom;
import di.uniba.map.b.adventure.type.MutableRoom;
import di.uniba.map.b.adventure.type.ObjEvent;
import di.uniba.map.b.adventure.type.PlayableRoom;
import di.uniba.map.b.adventure.type.RoomEvent;

/**
 * RoomsLoader
 */
public class RoomsLoader implements Runnable {

    private List<AbstractRoom> rooms;
    private boolean exceptionThrown = false;

    private List<Thread> workers = new ArrayList<>();

    public boolean isExceptionThrown() {
        return exceptionThrown;
    }

    public RoomsLoader(List<AbstractRoom> rooms) {
        this.rooms = rooms;
    }

    @Override
    public void run() {
        RuntimeTypeAdapterFactory<AbstractEntity> typeAdapterObjects =
                RuntimeTypeAdapterFactory
                        .of(AbstractEntity.class)
                        .registerSubtype(BasicObject.class)
                        .registerSubtype(BasicItem.class)
                        .registerSubtype(BasicContainer.class)
                        .registerSubtype(WearableContainer.class)
                        .registerSubtype(ChestlikeContainer.class)
                        .registerSubtype(SocketlikeContainer.class)
                        .registerSubtype(UnopenableDoor.class)
                        .registerSubtype(Door.class)
                        .registerSubtype(InvisibleWall.class)
                        .registerSubtype(WearableItem.class)
                        .registerSubtype(FillableItem.class)
                        .registerSubtype(ReadableItem.class)
                        .registerSubtype(LightSourceItem.class)
                        .registerSubtype(MovableObject.class)
                        .registerSubtype(PullableObject.class)
                        .registerSubtype(PushableObject.class)
                        .registerSubtype(FireObject.class)
                        .registerSubtype(FluidItem.class)
                        .registerSubtype(Human.class);

        RuntimeTypeAdapterFactory<AbstractRoom> typeAdapterRooms = RuntimeTypeAdapterFactory
                .of(AbstractRoom.class)
                .registerSubtype(PlayableRoom.class)
                .registerSubtype(MutableRoom.class)
                .registerSubtype(CutsceneRoom.class);

        RuntimeTypeAdapterFactory<AdvEvent> typeAdapterEvents =
                RuntimeTypeAdapterFactory
                        .of(AdvEvent.class)
                        .registerSubtype(ObjEvent.class)
                        .registerSubtype(RoomEvent.class);

        Gson gson = new GsonBuilder()
                .setExclusionStrategies(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        return AbstractRoom.class.equals(f.getDeclaredClass())
                                || AbstractEntity.class.equals(f.getDeclaredClass());
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                })
                .registerTypeAdapterFactory(typeAdapterObjects)
                .registerTypeAdapterFactory(typeAdapterRooms)
                .registerTypeAdapterFactory(typeAdapterEvents)
                .create();
        Type roomsType = new TypeToken<List<AbstractRoom>>() {}.getType();

        try (BufferedReader in = new BufferedReader(new InputStreamReader(
                new FileInputStream("./resources/rooms.json"), StandardCharsets.UTF_8))) {
            rooms.addAll(gson.fromJson(in, roomsType));

            linkRooms();

            Multimap<Integer, AbstractEntity> objects = mapAllObjects();

            for (AbstractEntity obj : objects.values()) {
                obj.processReferences(objects, rooms);
            }

            for (Thread worker : workers) {
                worker.join();
            }
        } catch (Exception e) {
            exceptionThrown = true;
            throw new RuntimeException(e);
        }
    }

    private List<AbstractRoom> listAllRooms() {
        List<AbstractRoom> result = new ArrayList<>();

        for (AbstractRoom room : rooms) {
            if (room instanceof MutableRoom) {
                MutableRoom mRoom = (MutableRoom) room;

                result.addAll(mRoom.getAllRooms());
            }

            result.add(room);
        }

        return result;
    }

    private Multimap<Integer, AbstractEntity> mapAllObjects() {

        Multimap<Integer, AbstractEntity> objects = ArrayListMultimap.create();
        Multimap<Integer, AbstractEntity> result = ArrayListMultimap.create();

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
