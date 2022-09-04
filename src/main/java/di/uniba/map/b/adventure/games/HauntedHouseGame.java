package di.uniba.map.b.adventure.games;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import di.uniba.map.b.adventure.GameDescription;
import di.uniba.map.b.adventure.GameJFrame;
import di.uniba.map.b.adventure.RuntimeTypeAdapterFactory;
import di.uniba.map.b.adventure.entities.AbstractEntity;
import di.uniba.map.b.adventure.entities.AdvDoorBlocked;
import di.uniba.map.b.adventure.entities.AdvDoorOpenable;
import di.uniba.map.b.adventure.entities.AdvFire;
import di.uniba.map.b.adventure.entities.AdvFixedObject;
import di.uniba.map.b.adventure.entities.AdvMagicWall;
import di.uniba.map.b.adventure.entities.AdvObjectMovable;
import di.uniba.map.b.adventure.entities.AdvObjectPullable;
import di.uniba.map.b.adventure.entities.AdvObjectPushable;
import di.uniba.map.b.adventure.entities.AdvPerson;
import di.uniba.map.b.adventure.entities.IFluid;
import di.uniba.map.b.adventure.entities.IMovable;
import di.uniba.map.b.adventure.entities.IOpenable;
import di.uniba.map.b.adventure.entities.IPickupable;
import di.uniba.map.b.adventure.entities.IPullable;
import di.uniba.map.b.adventure.entities.IPushable;
import di.uniba.map.b.adventure.entities.ISwitch;
import di.uniba.map.b.adventure.entities.ITalkable;
import di.uniba.map.b.adventure.entities.IWearable;
import di.uniba.map.b.adventure.entities.container.AbstractContainer;
import di.uniba.map.b.adventure.entities.container.AdvChest;
import di.uniba.map.b.adventure.entities.container.AdvContainer;
import di.uniba.map.b.adventure.entities.container.AdvSocket;
import di.uniba.map.b.adventure.entities.container.pickupable.AdvWearableContainer;
import di.uniba.map.b.adventure.entities.pickupable.AdvFillableItem;
import di.uniba.map.b.adventure.entities.pickupable.AdvFluid;
import di.uniba.map.b.adventure.entities.pickupable.AdvItem;
import di.uniba.map.b.adventure.entities.pickupable.AdvLightSource;
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

    StringBuilder outString = new StringBuilder();

    @Override
    public void init() throws FileNotFoundException, IOException {
        outString = new StringBuilder();
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
                        .registerSubtype(AdvLightSource.class)
                        .registerSubtype(AdvObjectMovable.class)
                        .registerSubtype(AdvObjectPullable.class)
                        .registerSubtype(AdvObjectPushable.class)
                        .registerSubtype(AdvFire.class)
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

            linkRooms(unpackRooms(rooms));

            for (Room room : rooms) {
                getRooms().add(room);
            }

            List<AbstractEntity> objects = listAllObjects();

            for (AbstractEntity obj : objects) {
                if (obj.getRequiredWearedItemsIdToInteract() != null) {
                    if (!obj.getRequiredWearedItemsIdToInteract().isEmpty()) {
                        obj.setRequiredWearedItemsToInteract(new ArrayList<>());

                        for (Integer objId : obj.getRequiredWearedItemsIdToInteract()) {
                            objects.stream()
                                    .filter(IWearable.class::isInstance)
                                    .filter(reqItem -> reqItem.getId() == objId)
                                    .forEach(reqItem -> obj.getRequiredWearedItemsToInteract()
                                            .add((IWearable) reqItem));
                        }
                    }
                }

                obj.processReferences(objects, rooms);
            }

            setCurrentRoom(rooms.get(0));
        }
    }

    private List<Room> unpackRooms(List<Room> rooms) {
        List<Room> result = new ArrayList<>();

        for (Room room : rooms) {
            result.addAll(unpackRoom(room));
        }

        return result;
    }

    private List<Room> unpackRoom(Room room) {
        List<Room> result = new ArrayList<>();

        if (room instanceof MutablePlayableRoom) {
            MutablePlayableRoom mRoom = (MutablePlayableRoom) room;

            if (mRoom.getNewRoom() != null) {
                result.addAll(unpackRoom(mRoom.getNewRoom()));
            }
        }

        result.add(room);
        return result;
    }

    private List<AbstractEntity> listAllObjects() {
        List<AbstractEntity> objects = new ArrayList<>();

        for (Room room : unpackRooms(getRooms())) {
            if (room instanceof PlayableRoom) {
                PlayableRoom pRoom = (PlayableRoom) room;

                if (pRoom.getObjects() != null) {
                    objects.addAll(pRoom.getObjects());
                }
            }
        }
        objects.addAll(unpackContainers(objects));
        return objects;
    }

    private List<AbstractEntity> unpackContainers(List<AbstractEntity> objects) {
        List<AbstractEntity> result = new ArrayList<>();

        for (AbstractEntity obj : objects) {
            if (obj instanceof AbstractContainer) {
                AbstractContainer container = (AbstractContainer) obj;

                result.addAll(unpackContainer(container));
            }
        }
        return result;
    }

    private List<AbstractEntity> unpackContainer(AbstractContainer container) {
        List<AbstractEntity> result = new ArrayList<>();

        if (container.getList() != null) {
            for (AbstractEntity obj : container.getList()) {
                if (obj instanceof AbstractContainer) {
                    result.addAll(unpackContainer((AbstractContainer) obj));
                }
                result.add(obj);
            }
        }
        return result;
    }

    private AdvMagicWall getMagicWall(PlayableRoom currentRoom, CommandType direction,
            Room nextRoom) {
        if (currentRoom.getObjects() != null) {
            for (AbstractEntity obj : currentRoom.getObjects()) {

                if (obj instanceof AdvMagicWall) {
                    AdvMagicWall wall = (AdvMagicWall) obj;
                    wall.processRequirements(getInventory());
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
        PlayableRoom currentRoom = (PlayableRoom) getCurrentRoom();

        // TODO controlla tutti i getList e il check != null altrimenti da nullPointer

        if (p.getCommand().getType() == CommandType.NORTH) {
            AdvMagicWall wall =
                    getMagicWall(currentRoom, CommandType.NORTH, currentRoom.getNorth());
            if (wall != null) {
                outString.append(wall.getTrespassingWhenLockedText());
            } else {
                moveTo(currentRoom.getNorth());
            }
        } else if (p.getCommand().getType() == CommandType.NORTH_EAST) {
            AdvMagicWall wall =
                    getMagicWall(currentRoom, CommandType.NORTH_EAST, currentRoom.getNorthEast());
            if (wall != null) {
                outString.append(wall.getTrespassingWhenLockedText());
            } else {
                moveTo(currentRoom.getNorthEast());
            }
        } else if (p.getCommand().getType() == CommandType.NORTH_WEST) {
            AdvMagicWall wall =
                    getMagicWall(currentRoom, CommandType.NORTH_WEST, currentRoom.getNorthWest());
            if (wall != null) {
                outString.append(wall.getTrespassingWhenLockedText());
            } else {
                moveTo(currentRoom.getNorthWest());
            }
        } else if (p.getCommand().getType() == CommandType.SOUTH) {
            AdvMagicWall wall =
                    getMagicWall(currentRoom, CommandType.SOUTH, currentRoom.getSouth());
            if (wall != null) {
                outString.append(wall.getTrespassingWhenLockedText());
            } else {
                moveTo(currentRoom.getSouth());
            }
        } else if (p.getCommand().getType() == CommandType.SOUTH_EAST) {
            AdvMagicWall wall =
                    getMagicWall(currentRoom, CommandType.SOUTH_EAST, currentRoom.getSouthEast());
            if (wall != null) {
                outString.append(wall.getTrespassingWhenLockedText());
            } else {
                moveTo(currentRoom.getSouthEast());
            }
        } else if (p.getCommand().getType() == CommandType.SOUTH_WEST) {
            AdvMagicWall wall =
                    getMagicWall(currentRoom, CommandType.SOUTH_WEST, currentRoom.getSouthWest());
            if (wall != null) {
                outString.append(wall.getTrespassingWhenLockedText());
            } else {
                moveTo(currentRoom.getSouthWest());
            }
        } else if (p.getCommand().getType() == CommandType.EAST) {
            AdvMagicWall wall = getMagicWall(currentRoom, CommandType.EAST, currentRoom.getEast());
            if (wall != null) {
                outString.append(wall.getTrespassingWhenLockedText());
            } else {
                moveTo(currentRoom.getEast());
            }
        } else if (p.getCommand().getType() == CommandType.WEST) {
            AdvMagicWall wall = getMagicWall(currentRoom, CommandType.WEST, currentRoom.getWest());
            if (wall != null) {
                outString.append(wall.getTrespassingWhenLockedText());
            } else {
                moveTo(currentRoom.getWest());
            }
        } else if (p.getCommand().getType() == CommandType.UP) {
            AdvMagicWall wall = getMagicWall(currentRoom, CommandType.UP, currentRoom.getUp());
            if (wall != null) {
                outString.append(wall.getTrespassingWhenLockedText());
            } else {
                moveTo(currentRoom.getUp());
            }
        } else if (p.getCommand().getType() == CommandType.DOWN) {
            AdvMagicWall wall = getMagicWall(currentRoom, CommandType.DOWN, currentRoom.getDown());
            if (wall != null) {
                outString.append(wall.getTrespassingWhenLockedText());
            } else {
                moveTo(currentRoom.getDown());
            }
        } else if (p.getCommand().getType() == CommandType.INVENTORY) {
            if (!getInventory().isEmpty()) {
                outString.append("Nel tuo inventario ci sono:");

                for (AbstractEntity obj : getInventory()) {
                    outString.append("<br> - " + obj.getName());
                }
            } else {
                outString.append("Il tuo inventario è vuoto.");
            }
        }

        AbstractEntity roomObj = p.getObject();
        AbstractEntity invObj = p.getInvObject();

        if (p.getCommand().getType() == CommandType.LOOK_AT) {
            AbstractEntity obj = getObjectFromParser(p);

            if (obj != null) {
                outString.append(obj.getLookMessage());
            } else {
                outString.append("Non trovo cosa esaminare.");
            }
        } else if (p.getCommand().getType() == CommandType.PICK_UP) {
            AbstractEntity obj = getObjectFromParser(p);

            if (obj != null) {
                if (obj instanceof IPickupable) {
                    IPickupable pickupObj = (IPickupable) obj;

                    outString.append(pickupObj.pickup(getInventory(), currentRoom.getObjects()));
                } else {
                    outString.append("Non puoi raccogliere questo oggetto.");
                }
            } else {
                outString.append("Non trovo cosa raccogliere.");
            }
        } else if (p.getCommand().getType() == CommandType.OPEN) {
            AbstractEntity obj = getObjectFromParser(p);

            if (obj != null) {
                if (obj instanceof IOpenable) {
                    IOpenable openableObj = (IOpenable) obj;

                    outString.append(openableObj.open(invObj));
                } else if (obj instanceof AdvDoorBlocked) {
                    AdvDoorBlocked fakeDoor = (AdvDoorBlocked) obj;

                    outString.append(fakeDoor.getOpenEventText());
                } else {
                    outString.append("Non puoi aprire " + obj.getName());
                }
            } else {
                outString.append("Non trovo cosa aprire.");
            }
        } else if (p.getCommand().getType() == CommandType.PUSH) {
            AbstractEntity obj = getObjectFromParser(p);

            if (obj != null) {
                if (obj instanceof IPushable) {
                    IPushable pushableObj = (IPushable) obj;

                    outString.append(pushableObj.push());
                } else {
                    outString.append("Non puoi premere " + obj.getName());
                }
            } else {
                outString.append("Non trovo cosa premere.");
            }
        } else if (p.getCommand().getType() == CommandType.PULL) {
            AbstractEntity obj = getObjectFromParser(p);

            if (obj != null) {
                if (obj instanceof IPullable) {
                    IPullable pullableObj = (IPullable) obj;

                    outString.append(pullableObj.pull());
                } else {
                    outString.append("Non puoi tirare " + obj.getName());
                }
            } else {
                outString.append("Non trovo cosa tirare.");
            }
        } else if (p.getCommand().getType() == CommandType.MOVE) {
            if (roomObj != null) {
                if (roomObj instanceof IMovable) {
                    IMovable movableObj = (IMovable) roomObj;

                    outString.append(movableObj.move());
                } else {
                    outString.append("Non puoi spostare " + roomObj.getName());
                }
            } else {
                outString.append("Non trovo l'oggetto da spostare.");
            }
        } else if (p.getCommand().getType() == CommandType.INSERT) {
            if (roomObj != null && invObj != null) {
                if (roomObj instanceof AbstractContainer) {
                    AbstractContainer container = (AbstractContainer) roomObj;

                    outString.append(container.insert(invObj, getInventory()));
                }
            } else if (roomObj == null && invObj != null) {
                outString.append("Non ho capito dove inserire.");
            } else if (roomObj != null && invObj == null) {
                outString.append("Non ho capito cosa inserire.");
            } else {
                outString.append("Non ho capito cosa devo fare.");
            }
        } else if (p.getCommand().getType() == CommandType.WEAR) {
            if (roomObj != null) {
                if (roomObj instanceof IWearable) {
                    outString.append("Devi prima prenderlo per poterlo indossare.");
                } else {
                    outString.append("Non puoi indossarlo.");
                }
            } else if (invObj != null) {
                if (invObj instanceof IWearable) {
                    IWearable wearable = (IWearable) invObj;

                    outString.append(wearable.wear());
                } else {
                    outString.append("Non puoi indossarlo.");
                }
            } else {
                outString.append("Non trovo l'oggetto da indossare.");
            }
        } else if (p.getCommand().getType() == CommandType.TURN_ON) {
            AbstractEntity obj = getObjectFromParser(p);

            if (obj != null) {
                if (obj instanceof ISwitch) {
                    ISwitch switchObj = (ISwitch) obj;

                    outString.append(switchObj.turnOn());
                } else {
                    outString.append("Non puoi accenderlo.");
                }
            } else {
                outString.append("Non trovo l'oggetto da accendere.");
            }
        } else if (p.getCommand().getType() == CommandType.TURN_OFF) {
            AbstractEntity obj = getObjectFromParser(p);

            if (obj != null) {
                if (obj instanceof ISwitch) {
                    ISwitch switchObj = (ISwitch) obj;

                    outString.append(switchObj.turnOff());
                } else if (obj instanceof AdvFire) {
                    AdvFire fire = (AdvFire) obj;

                    if (invObj instanceof IFluid) {
                        IFluid fluid = (IFluid) invObj;

                        outString.append(fire.extinguish(fluid));
                    } else if (invObj != null) {
                        outString.append("Non puoi spegnerlo con quello. ");
                    } else {
                        outString.append("Non puoi spegnerlo senza qualcosa di adatto.");
                    }
                } else {
                    outString.append("Non puoi spegnerlo.");
                }
            } else {
                outString.append("Non trovo l'oggetto da spegnere.");
            }
        } else if (p.getCommand().getType() == CommandType.TALK_TO) {
            if (roomObj != null) {
                if (roomObj instanceof ITalkable) {
                    ITalkable talkableObj = (ITalkable) roomObj;

                    outString.append(talkableObj.talk());
                } else {
                    outString.append("Non puoi parlarci.");
                }
            } else {
                outString.append("Non trovo con chi parlare.");
            }
        } else if (p.getCommand().getType() == CommandType.POUR) {
            if (invObj != null) {
                if (invObj instanceof IFluid) {
                    if (roomObj != null) {
                        if (roomObj instanceof AdvFire) {
                            IFluid fluid = (IFluid) invObj;
                            AdvFire fire = (AdvFire) roomObj;

                            outString.append(fire.extinguish(fluid));
                        } else if (roomObj instanceof AbstractContainer) {
                            AbstractContainer container = (AbstractContainer) roomObj;

                            outString.append(container.insert(invObj, getInventory()));
                        } else {
                            outString.append("Non puoi versarci il liquido.");
                        }
                    } else {
                        outString.append("Non trovo dove versare il liquido.");
                    }
                } else {
                    outString.append("Non posso versare qualcosa che non sia liquido.");
                }
            } else {
                outString.append("Non trovo cosa versare.");
            }
        }

        if (getStatus().isMovementAttempt()) {
            if (currentRoom.isDark()) {
                outString.append("Meglio non avventurarsi nel buio.");
            } else if (getStatus().isRoomBlockedByDoor()) {
                outString.append("La porta é chiusa.");
            } else if (getStatus().isPositionChanged()) {
                outString.append(getCurrentRoom().getDescription());
                outString.append(handleRoomEvent());
            } else {
                outString.append("Da quella parte non si puó andare.");
            }
        }

        if (isActionPerformed(p)) {
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

        if (roomObj != null) {
            if (roomObj.getEvents() != null) {
                Iterator<ObjEvent> it = roomObj.getEvents().iterator();

                while (it.hasNext()) {
                    ObjEvent evt = it.next();

                    if (evt.isTriggered()) {
                        if (evt.getTeleportsPlayerToRoom() != null) {
                            getStatus().setWarp(true);
                            getStatus().setWarpDestination(evt.getTeleportsPlayerToRoom());
                        }
                        it.remove();
                    }
                }
            }
        } else if (invObj != null) {
            if (invObj.getEvents() != null) {
                Iterator<ObjEvent> it = invObj.getEvents().iterator();

                while (it.hasNext()) {
                    ObjEvent evt = it.next();

                    if (evt.isTriggered()) {
                        if (evt.getTeleportsPlayerToRoom() != null) {
                            getStatus().setWarp(true);
                            getStatus().setWarpDestination(evt.getTeleportsPlayerToRoom());
                        }
                        it.remove();
                    }
                }
            }
        }

        if (getStatus().isWarp()) {
            setPreviousRoom(getCurrentRoom());
            setCurrentRoom(getStatus().getWarpDestination());
            outString.append("<br><br>" + getCurrentRoom().getDescription());
            outString.append(handleRoomEvent());
        }

        gui.appendTextEdtOutput(outString.toString(), false);

        outString.setLength(0);
        getStatus().reset();
    }

    private boolean isActionPerformed(ParserOutput p) {
        if (getStatus().isMovementAttempt() && getStatus().isPositionChanged()) {
            return true;
        } else if (p.getObject() != null && p.getObject().isActionPerformed()) {
            p.getObject().setActionPerformed(false);
            return true;
        } else if (p.getInvObject() != null && p.getInvObject().isActionPerformed()) {
            p.getInvObject().setActionPerformed(false);
            return true;
        } else {
            return false;
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
        rooms.stream()
                .filter(clazz::isInstance)
                .map(clazz::cast)
                .filter(room -> directionIdGetter.apply(room) != null)
                .forEach(room -> rooms.stream()
                        .filter(linkedRoom -> linkedRoom.getId() == directionIdGetter.apply(room))
                        .forEach(linkedRoom -> directionSetter.accept(room, linkedRoom)));
    }

    private void moveTo(Room room) {
        PlayableRoom currentRoom = (PlayableRoom) getCurrentRoom();

        if (!currentRoom.isDark()) {
            if (room != null) {

                if (currentRoom.getObjects() != null) {
                    for (AbstractEntity obj : currentRoom.getObjects()) {
                        if (obj instanceof AdvDoorOpenable) {
                            AdvDoorOpenable door = (AdvDoorOpenable) obj;
                            if (door.getBlockedRoomId() == room.getId() && door.isOpen()) {
                                setPreviousRoom(currentRoom);
                                setCurrentRoom(room);
                                getStatus().setPositionChanged(true);
                                return;
                            } else if (door.getBlockedRoomId() == room.getId() && !door.isOpen()) {
                                getStatus().setPositionChanged(false, true);
                                return;
                            } else if (door.getBlockedRoomId() == room.getId()
                                    && !room.isVisible()) {
                                getStatus().setPositionChanged(false);
                                return;
                            }
                        }
                    }
                }
                setPreviousRoom(currentRoom);
                setCurrentRoom(room);
                getStatus().setPositionChanged(true);
                return;
            } else {
                getStatus().setPositionChanged(false);
                return;
            }
        } else {
            if (room != null) {
                if (room.equals(getPreviousRoom())) {
                    setPreviousRoom(currentRoom);
                    setCurrentRoom(room);
                    getStatus().setPositionChanged(true);
                    return;
                }
            } else {
                getStatus().setPositionChanged(false);
            }
        }
    }
}
