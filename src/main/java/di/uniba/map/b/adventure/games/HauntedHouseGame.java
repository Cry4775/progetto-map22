package di.uniba.map.b.adventure.games;

import java.awt.Image;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.swing.ImageIcon;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import di.uniba.map.b.adventure.GameDescription;
import di.uniba.map.b.adventure.GameJFrame;
import di.uniba.map.b.adventure.RuntimeTypeAdapterFactory;
import di.uniba.map.b.adventure.Triple;
import di.uniba.map.b.adventure.entities.AdvDoorBlocked;
import di.uniba.map.b.adventure.entities.AdvDoorOpenable;
import di.uniba.map.b.adventure.entities.AdvFixedObject;
import di.uniba.map.b.adventure.entities.AdvMagicWall;
import di.uniba.map.b.adventure.entities.AbstractEntity;
import di.uniba.map.b.adventure.entities.AdvObjectMovable;
import di.uniba.map.b.adventure.entities.AdvObjectPullable;
import di.uniba.map.b.adventure.entities.AdvObjectPushable;
import di.uniba.map.b.adventure.entities.AdvPerson;
import di.uniba.map.b.adventure.entities.IMovable;
import di.uniba.map.b.adventure.entities.IOpenable;
import di.uniba.map.b.adventure.entities.IPickupable;
import di.uniba.map.b.adventure.entities.IPullable;
import di.uniba.map.b.adventure.entities.IPushable;
import di.uniba.map.b.adventure.entities.IWearable;
import di.uniba.map.b.adventure.entities.container.AbstractContainer;
import di.uniba.map.b.adventure.entities.container.AdvChest;
import di.uniba.map.b.adventure.entities.container.AdvContainer;
import di.uniba.map.b.adventure.entities.container.AdvSocket;
import di.uniba.map.b.adventure.entities.container.pickupable.AdvWearableContainer;
import di.uniba.map.b.adventure.entities.pickupable.AdvItem;
import di.uniba.map.b.adventure.entities.pickupable.AdvFillableItem;
import di.uniba.map.b.adventure.entities.pickupable.AdvFluid;
import di.uniba.map.b.adventure.entities.pickupable.AdvWearableItem;
import di.uniba.map.b.adventure.parser.ParserOutput;
import di.uniba.map.b.adventure.type.AdvEvent;
import di.uniba.map.b.adventure.type.Command;
import di.uniba.map.b.adventure.type.CommandType;
import di.uniba.map.b.adventure.type.MutablePlayableRoom;
import di.uniba.map.b.adventure.type.NonPlayableRoom;
import di.uniba.map.b.adventure.type.ObjEvent;
import di.uniba.map.b.adventure.type.PlayableRoom;
import di.uniba.map.b.adventure.type.Room;
import di.uniba.map.b.adventure.type.RoomEvent;

// Se tutto viene caricato da file allora si può cancellare

// TODO il database puó essere usato come lista di tutti gli oggetti, poi dal json si imposta tutto
// tramite id
public class HauntedHouseGame extends GameDescription {

    @Override
    public void init() throws FileNotFoundException, IOException {
        loadCommands();
        loadRooms();
    }

    private void loadCommands() throws FileNotFoundException, IOException {
        Gson gson = new Gson();

        Type commandsType = new TypeToken<List<Command>>() {}.getType();

        try (BufferedReader in = new BufferedReader(new InputStreamReader(
                new FileInputStream("./resources/commands.json"), StandardCharsets.UTF_8))) {
            List<Command> commands = gson.fromJson(in, commandsType);

            for (Command command : commands) {
                getCommands().add(command);
            }
        }
    }

    private void loadRooms() throws FileNotFoundException, IOException {
        RuntimeTypeAdapterFactory<AbstractEntity> typeAdapterObjects =
                RuntimeTypeAdapterFactory
                        .of(AbstractEntity.class)
                        .registerSubtype(AdvFixedObject.class)
                        .registerSubtype(AdvItem.class)
                        .registerSubtype(AbstractContainer.class)
                        .registerSubtype(AdvContainer.class)
                        .registerSubtype(AdvWearableContainer.class)
                        .registerSubtype(AdvChest.class)
                        .registerSubtype(AdvSocket.class)
                        .registerSubtype(AdvDoorBlocked.class)
                        .registerSubtype(AdvDoorOpenable.class)
                        .registerSubtype(AdvMagicWall.class)
                        .registerSubtype(AdvWearableItem.class)
                        .registerSubtype(AdvFillableItem.class)
                        .registerSubtype(AdvObjectMovable.class)
                        .registerSubtype(AdvObjectPullable.class)
                        .registerSubtype(AdvObjectPushable.class)
                        .registerSubtype(AdvFluid.class)
                        .registerSubtype(AdvPerson.class);

        RuntimeTypeAdapterFactory<Room> typeAdapterRooms = RuntimeTypeAdapterFactory
                .of(Room.class)
                .registerSubtype(PlayableRoom.class)
                .registerSubtype(MutablePlayableRoom.class)
                .registerSubtype(NonPlayableRoom.class);

        RuntimeTypeAdapterFactory<AdvEvent> typeAdapterEvents =
                RuntimeTypeAdapterFactory
                        .of(AdvEvent.class)
                        .registerSubtype(ObjEvent.class)
                        .registerSubtype(RoomEvent.class);

        Gson gson = new GsonBuilder()
                .setExclusionStrategies(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        return Room.class.equals(f.getDeclaredClass())
                                || AdvItem.class.equals(f.getDeclaredClass());
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                })
                .registerTypeAdapterFactory(typeAdapterObjects)
                .registerTypeAdapterFactory(typeAdapterRooms)
                .registerTypeAdapterFactory(typeAdapterEvents).create();
        Type roomsType = new TypeToken<List<Room>>() {}.getType();

        try (BufferedReader in = new BufferedReader(new InputStreamReader(
                new FileInputStream("./resources/rooms.json"), StandardCharsets.UTF_8))) {
            List<Room> rooms = gson.fromJson(in, roomsType);

            linkRooms(rooms);

            for (Room room : rooms) {
                getRooms().add(room);
            }

            List<AbstractEntity> objects = listAllObjects();

            for (AbstractEntity advObject : objects) {
                linkObjectsParent(advObject);
                linkObjectsEventsReference(advObject, getRooms());
                linkDoorsBlockedRoom(advObject, getRooms());

                if (advObject instanceof AdvFillableItem) {
                    linkItemFillablesReference((AdvFillableItem) advObject, objects);
                } else if (advObject instanceof AdvSocket) {
                    linkAdvSocketReference((AdvSocket) advObject, objects);
                }
            }

            setCurrentRoom(rooms.get(0));
        }
    }

    private List<AbstractEntity> listAllObjects() {
        List<AbstractEntity> objects = new ArrayList<>();

        List<PlayableRoom> rooms =
                new ArrayList<>(getRooms().stream()
                        .filter(PlayableRoom.class::isInstance)
                        .map(PlayableRoom.class::cast)
                        .collect(Collectors.toList()));
        unpackMutableRooms(rooms);
        for (PlayableRoom room : rooms) {
            if (room.getObjects() != null) {
                objects.addAll(room.getObjects());
            }
        }

        unpackContainerObj(objects);
        return objects;
    }

    private void unpackMutableRooms(List<PlayableRoom> rooms) {
        List<PlayableRoom> tempRooms = new ArrayList<>();
        for (PlayableRoom room : rooms) {
            if (room instanceof MutablePlayableRoom) {
                MutablePlayableRoom mutableRoom = (MutablePlayableRoom) room;
                if (mutableRoom.getNewRoom() != null) {
                    tempRooms.add(mutableRoom.getNewRoom());
                    List<PlayableRoom> newRooms = new ArrayList<>();
                    newRooms.add(mutableRoom.getNewRoom());
                    unpackMutableRooms(newRooms);
                }
            }
        }
        rooms.addAll(tempRooms);
    }

    private void unpackContainerObj(List<AbstractEntity> objects) {
        List<AbstractEntity> tempObjects = new ArrayList<>();
        for (AbstractEntity obj : objects) {
            if (obj instanceof AbstractContainer) {
                AbstractContainer container = (AbstractContainer) obj;
                if (container.getList() != null) {
                    List<AbstractEntity> copyContainer = new ArrayList<>(container.getList());
                    unpackContainerObj(copyContainer);
                    tempObjects.addAll(copyContainer);
                }
            }
        }
        objects.addAll(tempObjects);
    }

    private void linkItemFillablesReference(AdvFillableItem obj, List<AbstractEntity> objects) {
        if (obj.getEligibleItemId() != null) {
            objects.stream()
                    .filter(AdvItem.class::isInstance).map(AdvItem.class::cast)
                    .filter(reqItem -> reqItem.getId() == obj.getEligibleItemId())
                    .forEach(reqItem -> obj.setEligibleItem(reqItem));
        }
    }

    private void linkAdvSocketReference(AdvSocket obj, List<AbstractEntity> objects) {
        if (obj.getEligibleItemId() != null) {
            objects.stream()
                    .filter(reqItem -> reqItem.getId() == obj.getEligibleItemId())
                    .forEach(reqItem -> obj.setEligibleItem(reqItem));
        }
    }

    private void linkObjectsParent(AbstractEntity obj) {
        if (obj instanceof AbstractContainer) {
            AbstractContainer container = (AbstractContainer) obj;
            if (container.getList() != null) {
                for (AbstractEntity item : container.getList()) {
                    item.setParent(container);
                    linkObjectsParent(item);
                }
            }
        }
    }

    private void linkDoorsBlockedRoom(AbstractEntity obj, List<Room> rooms) {
        if (obj instanceof AdvDoorOpenable) {
            AdvDoorOpenable door = (AdvDoorOpenable) obj;

            if (door.getBlockedRoomId() != 0) {
                rooms.stream()
                        .filter(Room.class::isInstance)
                        .map(Room.class::cast)
                        .filter(room -> door.getBlockedRoomId() == room.getId())
                        .forEach(room -> door.setBlockedRoom(room));
            }
        }
    }

    private void linkObjectsEventsReference(AbstractEntity obj, List<Room> rooms) {
        if (obj.getEvents() != null) {
            for (ObjEvent objEvent : obj.getEvents()) {
                if (objEvent.getUpdateTargetRoomId() != null) {
                    rooms.stream()
                            .filter(MutablePlayableRoom.class::isInstance)
                            .map(MutablePlayableRoom.class::cast)
                            .filter(room -> objEvent.getUpdateTargetRoomId() == room.getId())
                            .forEach(room -> objEvent.setUpdateTargetRoom(room));
                }

                if (objEvent.isUpdatingParentRoom()) {
                    rooms.stream()
                            .filter(MutablePlayableRoom.class::isInstance)
                            .map(MutablePlayableRoom.class::cast)
                            .filter(room -> room.getObjects().contains(obj))
                            .forEach(room -> objEvent.setParentRoom(room));

                    if (objEvent.getParentRoom() == null) {
                        rooms.stream()
                                .filter(MutablePlayableRoom.class::isInstance)
                                .map(MutablePlayableRoom.class::cast)
                                .filter(room -> room.getNewRoom() != null)
                                .filter(room -> linkParentRoomEvt(obj, room, objEvent))
                                .forEach(room -> objEvent.setParentRoom(room));
                    }
                }
            }
        }
    }

    private boolean linkParentRoomEvt(AbstractEntity obj, MutablePlayableRoom room,
            ObjEvent objEvent) {
        if (objEvent.getParentRoom() == null) {
            MutablePlayableRoom nextRoom = room.getNewRoom();
            if (nextRoom != null) {
                if (nextRoom.getObjects() != null
                        && nextRoom.getObjects().contains(obj)) {
                    return true;
                } else {
                    linkParentRoomEvt(obj, nextRoom, objEvent);
                }
            }
        }
        return false;
    }

    private AdvMagicWall getMagicWall(PlayableRoom currentRoom, CommandType direction,
            Room nextRoom) {
        if (currentRoom.getObjects() != null) {
            for (AbstractEntity obj : currentRoom.getObjects()) {

                if (obj instanceof AdvMagicWall) {
                    AdvMagicWall wall = (AdvMagicWall) obj;
                    if (wall.isDirectionBlocked(direction, nextRoom)) {
                        return wall;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public void nextMove(ParserOutput p, GameJFrame gui) {
        Triple<Boolean, Boolean, Boolean> triple = new Triple<>(false, false, false);
        boolean actionPerformed = false;
        PlayableRoom currentRoom = (PlayableRoom) getCurrentRoom();

        // TODO wear e put
        // TODO controlla tutti i getList e il check != null altrimenti da nullPointer

        if (p.getCommand().getType() == CommandType.NORTH) {
            AdvMagicWall wall =
                    getMagicWall(currentRoom, CommandType.NORTH, currentRoom.getNorth());
            if (wall != null) {
                gui.appendTextEdtOutput(wall.getTrespassingWhenLockedText(), false);
            } else {
                triple = moveToCardinalDirection(currentRoom.getNorth());
            }
        } else if (p.getCommand().getType() == CommandType.NORTH_EAST) {
            AdvMagicWall wall =
                    getMagicWall(currentRoom, CommandType.NORTH_EAST, currentRoom.getNorthEast());
            if (wall != null) {
                gui.appendTextEdtOutput(wall.getTrespassingWhenLockedText(), false);
            } else {
                triple = moveToCardinalDirection(currentRoom.getNorthEast());
            }
        } else if (p.getCommand().getType() == CommandType.NORTH_WEST) {
            AdvMagicWall wall =
                    getMagicWall(currentRoom, CommandType.NORTH_WEST, currentRoom.getNorthWest());
            if (wall != null) {
                gui.appendTextEdtOutput(wall.getTrespassingWhenLockedText(), false);
            } else {
                triple = moveToCardinalDirection(currentRoom.getNorthWest());
            }
        } else if (p.getCommand().getType() == CommandType.SOUTH) {
            AdvMagicWall wall =
                    getMagicWall(currentRoom, CommandType.SOUTH, currentRoom.getSouth());
            if (wall != null) {
                gui.appendTextEdtOutput(wall.getTrespassingWhenLockedText(), false);
            } else {
                triple = moveToCardinalDirection(currentRoom.getSouth());
            }
        } else if (p.getCommand().getType() == CommandType.SOUTH_EAST) {
            AdvMagicWall wall =
                    getMagicWall(currentRoom, CommandType.SOUTH_EAST, currentRoom.getSouthEast());
            if (wall != null) {
                gui.appendTextEdtOutput(wall.getTrespassingWhenLockedText(), false);
            } else {
                triple = moveToCardinalDirection(currentRoom.getSouthEast());
            }
        } else if (p.getCommand().getType() == CommandType.SOUTH_WEST) {
            AdvMagicWall wall =
                    getMagicWall(currentRoom, CommandType.SOUTH_WEST, currentRoom.getSouthWest());
            if (wall != null) {
                gui.appendTextEdtOutput(wall.getTrespassingWhenLockedText(), false);
            } else {
                triple = moveToCardinalDirection(currentRoom.getSouthWest());
            }
        } else if (p.getCommand().getType() == CommandType.EAST) {
            AdvMagicWall wall = getMagicWall(currentRoom, CommandType.EAST, currentRoom.getEast());
            if (wall != null) {
                gui.appendTextEdtOutput(wall.getTrespassingWhenLockedText(), false);
            } else {
                triple = moveToCardinalDirection(currentRoom.getEast());
            }
        } else if (p.getCommand().getType() == CommandType.WEST) {
            AdvMagicWall wall = getMagicWall(currentRoom, CommandType.WEST, currentRoom.getWest());
            if (wall != null) {
                gui.appendTextEdtOutput(wall.getTrespassingWhenLockedText(), false);
            } else {
                triple = moveToCardinalDirection(currentRoom.getWest());
            }
        } else if (p.getCommand().getType() == CommandType.UP) {
            AdvMagicWall wall = getMagicWall(currentRoom, CommandType.UP, currentRoom.getUp());
            if (wall != null) {
                gui.appendTextEdtOutput(wall.getTrespassingWhenLockedText(), false);
            } else {
                triple = moveToCardinalDirection(currentRoom.getUp());
            }
        } else if (p.getCommand().getType() == CommandType.DOWN) {
            AdvMagicWall wall = getMagicWall(currentRoom, CommandType.DOWN, currentRoom.getDown());
            if (wall != null) {
                gui.appendTextEdtOutput(wall.getTrespassingWhenLockedText(), false);
            } else {
                triple = moveToCardinalDirection(currentRoom.getDown());
            }
        } else if (p.getCommand().getType() == CommandType.INVENTORY) {
            if (!getInventory().isEmpty()) {
                StringBuilder outString = new StringBuilder("Nel tuo inventario ci sono:");
                for (AbstractEntity obj : getInventory()) {
                    outString.append("<br> - " + obj.getName());
                }
                gui.appendTextEdtOutput(outString.toString(), false);
            } else {
                gui.appendTextEdtOutput("Il tuo inventario è vuoto.", false);
            }
        } else if (p.getCommand().getType() == CommandType.LOOK_AT) {
            if (p.getObject() != null || p.getInvObject() != null) {
                AbstractEntity obj = getObjectFromParser(p);

                gui.appendTextEdtOutput(obj.getLookMessage().toString(), false);
            } else {
                gui.appendTextEdtOutput("Non trovo l'oggetto da esaminare.", false);
            }
        } else if (p.getCommand().getType() == CommandType.PICK_UP) {
            if (p.getObject() != null || p.getInvObject() != null) {
                AbstractEntity obj = getObjectFromParser(p);
                if (obj instanceof IPickupable) {
                    IPickupable pickupableObj = (IPickupable) obj;

                    StringBuilder outString = new StringBuilder();
                    actionPerformed =
                            pickupableObj.pickup(outString, getInventory(),
                                    currentRoom.getObjects());

                    gui.appendTextEdtOutput(outString.toString(), false);
                } else {
                    gui.appendTextEdtOutput("Non puoi raccogliere questo oggetto.", false);
                }
            } else {
                gui.appendTextEdtOutput("Non trovo l'oggetto da raccogliere.", false);
            }
        } else if (p.getCommand().getType() == CommandType.OPEN) {
            if (p.getObject() != null || p.getInvObject() != null) {
                AbstractEntity obj = getObjectFromParser(p);

                if (obj instanceof IOpenable) {
                    IOpenable openableObj = (IOpenable) obj;
                    AbstractEntity key = p.getInvObject();

                    StringBuilder outString = new StringBuilder();
                    actionPerformed = openableObj.open(outString, key);

                    gui.appendTextEdtOutput(outString.toString(), false);
                } else if (obj instanceof AdvDoorBlocked) {
                    AdvDoorBlocked fakeDoor = (AdvDoorBlocked) obj;
                    gui.appendTextEdtOutput(fakeDoor.getOpenEventText(), false);
                } else {
                    gui.appendTextEdtOutput("Non puoi aprire " + obj.getName(), false);
                }
            } else {
                gui.appendTextEdtOutput("Non trovo l'oggetto da aprire.", false);
            }
        } else if (p.getCommand().getType() == CommandType.PUSH) {
            if (p.getObject() != null) {
                AbstractEntity obj = getObjectFromParser(p);

                if (obj instanceof IPushable) {
                    IPushable pushableObj = (IPushable) obj;

                    StringBuilder outString = new StringBuilder();
                    actionPerformed = pushableObj.push(outString);

                    gui.appendTextEdtOutput(outString.toString(), false);
                } else {
                    gui.appendTextEdtOutput("Non puoi premere " + obj.getName(), false);
                }
            } else {
                gui.appendTextEdtOutput("Non trovo l'oggetto da premere.", false);
            }
        } else if (p.getCommand().getType() == CommandType.PULL) {
            if (p.getObject() != null) {
                AbstractEntity obj = getObjectFromParser(p);

                if (obj instanceof IPullable) {
                    IPullable pullableObj = (IPullable) obj;

                    StringBuilder outString = new StringBuilder();
                    actionPerformed = pullableObj.pull(outString);

                    gui.appendTextEdtOutput(outString.toString(), false);
                } else {
                    gui.appendTextEdtOutput("Non puoi tirare " + obj.getName(), false);
                }
            } else {
                gui.appendTextEdtOutput("Non trovo l'oggetto da tirare.", false);
            }
        } else if (p.getCommand().getType() == CommandType.MOVE) {
            if (p.getObject() != null) {
                AbstractEntity obj = getObjectFromParser(p);

                if (obj instanceof IMovable) {
                    IMovable movableObj = (IMovable) obj;

                    StringBuilder outString = new StringBuilder();
                    actionPerformed = movableObj.move(outString);

                    gui.appendTextEdtOutput(outString.toString(), false);
                } else {
                    gui.appendTextEdtOutput("Non puoi spostare " + obj.getName(), false);
                }
            } else {
                gui.appendTextEdtOutput("Non trovo l'oggetto da spostare.", false);
            }
        } else if (p.getCommand().getType() == CommandType.INSERT) {
            if (p.getObject() != null && p.getInvObject() != null) {
                AbstractEntity obj = p.getObject();
                AbstractEntity invObj = p.getInvObject();

                if (obj instanceof AbstractContainer) {
                    AbstractContainer container = (AbstractContainer) obj;

                    StringBuilder outString = new StringBuilder();
                    actionPerformed = container.insert(outString, invObj, getInventory());

                    gui.appendTextEdtOutput(outString.toString(), false);
                }
            } else if (p.getObject() == null && p.getInvObject() != null) {
                gui.appendTextEdtOutput("Non trovo l'oggetto in cui inserire.", false);
            } else if (p.getInvObject() == null && p.getObject() != null) {
                gui.appendTextEdtOutput("Non trovo l'oggetto da inserire.", false);
            } else {
                gui.appendTextEdtOutput("Non ho capito cosa devo fare.", false);
            }
        } else if (p.getCommand().getType() == CommandType.WEAR) {
            if (p.getObject() != null) {
                AbstractEntity obj = p.getObject();

                if (obj instanceof IWearable) {
                    gui.appendTextEdtOutput("Devi prima prenderlo per poterlo indossare.", false);
                } else {
                    gui.appendTextEdtOutput("Non puoi indossarlo.", false);
                }
            } else if (p.getInvObject() != null) {
                AbstractEntity invObj = p.getInvObject();

                if (invObj instanceof IWearable) {
                    IWearable wearable = (IWearable) invObj;
                    StringBuilder outString = new StringBuilder();

                    wearable.wear(outString);

                    gui.appendTextEdtOutput(outString.toString(), false);
                } else {
                    gui.appendTextEdtOutput("Non puoi indossarlo.", false);
                }
            } else {
                gui.appendTextEdtOutput("Non trovo l'oggetto da indossare.", false);
            }
        }

        if (triple.getFirst() || triple.getSecond() || triple.getThird()) {
            boolean move = triple.getFirst();
            boolean noRoom = triple.getSecond();
            boolean roomBlockedByDoor = triple.getThird();
            if (noRoom) {
                gui.appendTextEdtOutput("Da quella parte non si può andare.", false);
            } else if (roomBlockedByDoor) {
                gui.appendTextEdtOutput("La porta è chiusa.", false);
            } else if (move) {
                gui.getLblRoomName().setText(getCurrentRoom().getName());

                Image roomImg = new ImageIcon(getCurrentRoom().getImgPath()).getImage()
                        .getScaledInstance(581, 300, Image.SCALE_SMOOTH);
                gui.getLblRoomImage().setIcon(new ImageIcon(roomImg));
                setCompassLabels(gui);

                StringBuilder outString = new StringBuilder(getCurrentRoom().getDescription());

                outString.append(handleRoomEvent());

                gui.appendTextEdtOutput(outString.toString(), false);

                actionPerformed = true;
            }
        }
        if (actionPerformed) {
            String[] strings = gui.getLblActions().getText().split(".*: ");
            for (String string : strings) {
                if (string.matches("[0-9]+")) {
                    int oldCounterVal = Integer.parseInt(string);
                    gui.getLblActions().setText("Azioni: " + Integer.toString(oldCounterVal + 1));
                    break;
                }
            }

            getInventory().removeIf(obj -> obj.isMustDestroyFromInv());
        }
    }

    private AbstractEntity getObjectFromParser(ParserOutput p) {
        if (p.getObject() != null) {
            return p.getObject();
        } else if (p.getInvObject() != null) {
            return p.getInvObject();
        }
        return null;
    }

    private String handleRoomEvent() {
        PlayableRoom currentRoom;
        if (getCurrentRoom() instanceof PlayableRoom) {
            currentRoom = (PlayableRoom) getCurrentRoom();
            RoomEvent evt = currentRoom.getEvent();
            if (evt != null) {
                if (!evt.isTriggered()) {
                    StringBuilder outString = new StringBuilder();

                    if (evt.getText() != null && !evt.getText().isEmpty()) {
                        outString.append("<br><br>" + evt.getText());
                    }

                    evt.setTriggered(true);

                    return outString.toString();
                }
            }
        }
        return "";
    }

    private void linkRooms(List<Room> rooms) {
        setRoomsDirection(rooms, PlayableRoom::getEastId, PlayableRoom::setEast,
                PlayableRoom.class);
        setRoomsDirection(rooms, PlayableRoom::getWestId, PlayableRoom::setWest,
                PlayableRoom.class);
        setRoomsDirection(rooms, PlayableRoom::getNorthId, PlayableRoom::setNorth,
                PlayableRoom.class);
        setRoomsDirection(rooms, PlayableRoom::getSouthId, PlayableRoom::setSouth,
                PlayableRoom.class);
        setRoomsDirection(rooms, PlayableRoom::getNorthEastId, PlayableRoom::setNorthEast,
                PlayableRoom.class);
        setRoomsDirection(rooms, PlayableRoom::getNorthWestId, PlayableRoom::setNorthWest,
                PlayableRoom.class);
        setRoomsDirection(rooms, PlayableRoom::getSouthEastId, PlayableRoom::setSouthEast,
                PlayableRoom.class);
        setRoomsDirection(rooms, PlayableRoom::getSouthWestId, PlayableRoom::setSouthWest,
                PlayableRoom.class);
        setRoomsDirection(rooms, PlayableRoom::getUpId, PlayableRoom::setUp, PlayableRoom.class);
        setRoomsDirection(rooms, PlayableRoom::getDownId, PlayableRoom::setDown,
                PlayableRoom.class);
        setRoomsDirection(rooms, NonPlayableRoom::getNextRoomId, NonPlayableRoom::setNextRoom,
                NonPlayableRoom.class);
    }

    private <T extends Room> void setRoomsDirection(List<Room> rooms,
            Function<T, Integer> directionIdGetter, BiConsumer<T, Room> directionSetter,
            Class<T> clazz) {
        rooms.stream().filter(clazz::isInstance).map(clazz::cast)
                .filter(room -> directionIdGetter.apply(room) != null)
                .forEach(room -> rooms.stream()
                        .filter(linkedRoom -> linkedRoom.getId() == directionIdGetter.apply(room))
                        .forEach(linkedRoom -> directionSetter.accept(room, linkedRoom)));
    }

    private Triple<Boolean, Boolean, Boolean> moveToCardinalDirection(Room room) {
        PlayableRoom currentRoom = (PlayableRoom) getCurrentRoom();

        if (room != null) {

            if (currentRoom.getObjects() != null) {
                for (AbstractEntity obj : currentRoom.getObjects()) {
                    if (obj instanceof AdvDoorOpenable) {
                        AdvDoorOpenable door = (AdvDoorOpenable) obj;
                        if (door.getBlockedRoomId() == room.getId() && door.isOpen()) {
                            setCurrentRoom(room);
                            return new Triple<>(true, false, false);
                        } else if (door.getBlockedRoomId() == room.getId() && !door.isOpen()) {
                            return new Triple<>(false, false, true);
                        } else if (door.getBlockedRoomId() == room.getId()
                                && !room.isVisible()) {
                            return new Triple<>(false, true, false);
                        }
                    }
                }
            }
            setCurrentRoom(room);
            return new Triple<>(true, false, false);
        } else {
            return new Triple<>(false, true, false);
        }
    }
}
