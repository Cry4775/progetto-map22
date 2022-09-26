package di.uniba.map.b.adventure.games;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
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
import di.uniba.map.b.adventure.entities.ILightSource;
import di.uniba.map.b.adventure.entities.IMovable;
import di.uniba.map.b.adventure.entities.IOpenable;
import di.uniba.map.b.adventure.entities.IPickupable;
import di.uniba.map.b.adventure.entities.IPullable;
import di.uniba.map.b.adventure.entities.IPushable;
import di.uniba.map.b.adventure.entities.IReadable;
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
import di.uniba.map.b.adventure.entities.pickupable.AdvReadable;
import di.uniba.map.b.adventure.entities.pickupable.AdvWearableItem;
import di.uniba.map.b.adventure.parser.ParserOutput;
import di.uniba.map.b.adventure.type.AdvEvent;
import di.uniba.map.b.adventure.type.Command;
import di.uniba.map.b.adventure.type.CommandType;
import di.uniba.map.b.adventure.type.MutablePlayableRoom;
import di.uniba.map.b.adventure.type.CutsceneRoom;
import di.uniba.map.b.adventure.type.ObjEvent;
import di.uniba.map.b.adventure.type.PlayableRoom;
import di.uniba.map.b.adventure.type.AbstractRoom;
import di.uniba.map.b.adventure.type.RoomEvent;

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
                        .registerSubtype(AdvContainer.class)
                        .registerSubtype(AdvWearableContainer.class)
                        .registerSubtype(AdvChest.class)
                        .registerSubtype(AdvSocket.class)
                        .registerSubtype(AdvDoorBlocked.class)
                        .registerSubtype(AdvDoorOpenable.class)
                        .registerSubtype(AdvMagicWall.class)
                        .registerSubtype(AdvWearableItem.class)
                        .registerSubtype(AdvFillableItem.class)
                        .registerSubtype(AdvReadable.class)
                        .registerSubtype(AdvLightSource.class)
                        .registerSubtype(AdvObjectMovable.class)
                        .registerSubtype(AdvObjectPullable.class)
                        .registerSubtype(AdvObjectPushable.class)
                        .registerSubtype(AdvFire.class)
                        .registerSubtype(AdvFluid.class)
                        .registerSubtype(AdvPerson.class);

        RuntimeTypeAdapterFactory<AbstractRoom> typeAdapterRooms = RuntimeTypeAdapterFactory
                .of(AbstractRoom.class)
                .registerSubtype(PlayableRoom.class)
                .registerSubtype(MutablePlayableRoom.class)
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
            getRooms().addAll(gson.fromJson(in, roomsType));

            linkRooms();

            List<AbstractEntity> objects = listAllObjects();
            for (AbstractEntity obj : objects) {
                obj.processReferences(objects, getRooms());
            }

            setCurrentRoom(getRooms().get(0));
        }
    }

    private List<AbstractRoom> listAllRooms() {
        List<AbstractRoom> result = new ArrayList<>();

        for (AbstractRoom room : getRooms()) {
            if (room instanceof MutablePlayableRoom) {
                MutablePlayableRoom mRoom = (MutablePlayableRoom) room;

                result.addAll(mRoom.getAllRooms());
            }

            result.add(room);
        }

        return result;
    }

    private List<AbstractEntity> listAllObjects() {
        List<AbstractEntity> objects = new ArrayList<>();
        List<AbstractEntity> result = new ArrayList<>();

        for (AbstractRoom room : listAllRooms()) {
            if (room instanceof PlayableRoom) {
                PlayableRoom pRoom = (PlayableRoom) room;

                if (pRoom.getObjects() != null) {
                    objects.addAll(pRoom.getObjects());
                }
            }
        }

        result.addAll(objects);

        for (AbstractEntity obj : objects) {
            if (obj instanceof AbstractContainer) {
                AbstractContainer container = (AbstractContainer) obj;

                result.addAll(container.getAllObjects());
            }
        }

        return result;
    }

    public boolean isPossibleToMove(CommandType direction) {
        PlayableRoom currentRoom = (PlayableRoom) getCurrentRoom();

        getStatus().setMovementAttempt(true);

        AdvMagicWall wall = currentRoom.getMagicWall(direction);
        if (wall != null) {
            wall.processRequirements(getInventory());

            if (wall.isLocked()) {
                getStatus().setRoomBlockedByWall(true);
                getStatus().setWall(wall);
                return false;
            } else {
                return true;
            }
        }

        return true;
    }

    public boolean isMovementCommand(CommandType commandType) {
        switch (commandType) {
            case NORTH:
                return true;
            case NORTH_EAST:
                return true;
            case NORTH_WEST:
                return true;
            case SOUTH:
                return true;
            case SOUTH_EAST:
                return true;
            case SOUTH_WEST:
                return true;
            case EAST:
                return true;
            case WEST:
                return true;
            case UP:
                return true;
            case DOWN:
                return true;
            default:
                return false;
        }
    }

    @Override
    public void nextMove(ParserOutput p, GameJFrame gui) {
        PlayableRoom currentRoom = (PlayableRoom) getCurrentRoom();

        // TODO controlla tutti i getList e il check != null altrimenti da nullPointer
        CommandType commandType = p.getCommand().getType();

        if (isMovementCommand(commandType)) {
            if (isPossibleToMove(commandType)) {
                moveTo(currentRoom.getRoomAt(commandType));
            }
        } else if (p.getCommand().getType() == CommandType.INVENTORY) {
            if (!getInventory().isEmpty()) {
                outString.append("Nel tuo inventario ci sono:");

                for (AbstractEntity obj : getInventory()) {
                    outString.append("<br> - " + obj.getName());
                    if (obj instanceof IWearable) {
                        IWearable wearable = (IWearable) obj;

                        outString.append(wearable.isWorn() ? " (INDOSSATO)" : "");
                    }
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

                    outString.append(pickupObj.pickup(getInventory()));
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
        } else if (p.getCommand().getType() == CommandType.UNWEAR) {
            if (roomObj != null) {
                outString.append("Non posso togliermi qualcosa che non ho addosso.");
            } else if (invObj != null) {
                if (invObj instanceof IWearable) {
                    IWearable wearable = (IWearable) invObj;

                    outString.append(wearable.unwear());
                } else {
                    outString.append("Non puoi farlo.");
                }
            } else {
                outString.append("Non trovo l'oggetto da togliere.");
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
        } else if (p.getCommand().getType() == CommandType.READ) {
            AbstractEntity obj = getObjectFromParser(p);

            if (obj != null) {
                if (obj instanceof IReadable) {
                    IReadable readableObj = (IReadable) obj;

                    outString.append(readableObj.read());
                } else {
                    outString.append("Non posso leggerlo.");
                }
            } else {
                outString.append("Non trovo cosa leggere.");
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

        processTriggeredEvents(roomObj);
        processTriggeredEvents(invObj);

        if (getCurrentRoom() instanceof PlayableRoom) {
            currentRoom = (PlayableRoom) getCurrentRoom();
            processRoomLighting(currentRoom);
        } else {
            currentRoom = null;
        }

        if (getStatus().isMovementAttempt()) {
            if (!getStatus().isPositionChanged() && currentRoom.isCurrentlyDark()) {
                outString.append("Meglio non avventurarsi nel buio.");
            } else if (getStatus().isRoomBlockedByDoor()) {
                outString.append("La porta é chiusa.");
            } else if (getStatus().isRoomBlockedByWall()) {
                outString.append(getStatus().getWall().getTrespassingWhenLockedText());
            } else if (getStatus().isPositionChanged()) {
                if (currentRoom != null && currentRoom.isCurrentlyDark()) {
                    outString.append("È completamente buio e non riesci a vedere niente.");
                } else {
                    outString.append(getCurrentRoom().getDescription());
                    outString.append(handleRoomEvent());
                }
            } else {
                outString.append("Da quella parte non si puó andare.");
            }
        }

        if (getStatus().isWarp()) {
            setPreviousRoom(getCurrentRoom());
            setCurrentRoom(getStatus().getWarpDestination());

            outString.append(outString.length() > 0 ? "<br><br>" : "");
            outString.append(getCurrentRoom().getDescription());
            outString.append(handleRoomEvent());
        }

        if (outString.length() > 0) {
            gui.appendTextEdtOutput(outString.toString(), false);
        }

        outString.setLength(0);
        getStatus().reset();
    }

    private void processRoomLighting(PlayableRoom currentRoom) {
        if (currentRoom.isDarkByDefault()) {
            if (currentRoom.isCurrentlyDark()) {
                for (AbstractEntity obj : getInventory()) {
                    if (obj instanceof ILightSource) {
                        ILightSource light = (ILightSource) obj;
                        if (light.isOn()) {
                            currentRoom.setCurrentlyDark(false);
                            break;
                        }
                    }
                }
            } else {
                for (AbstractEntity obj : getInventory()) {
                    if (obj instanceof ILightSource) {
                        ILightSource light = (ILightSource) obj;
                        if (!light.isOn()) {
                            currentRoom.setCurrentlyDark(true);
                            break;
                        }
                    }
                }
            }
        }
    }

    private void processTriggeredEvents(AbstractEntity obj) {
        if (obj != null) {
            if (obj.getEvents() != null) {
                Iterator<ObjEvent> it = obj.getEvents().iterator();

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

    private void linkRooms() {
        List<AbstractRoom> allRooms = listAllRooms();

        setRoomsDirection(allRooms, PlayableRoom::getEastId, PlayableRoom::setEast,
                PlayableRoom.class);
        setRoomsDirection(allRooms, PlayableRoom::getWestId, PlayableRoom::setWest,
                PlayableRoom.class);
        setRoomsDirection(allRooms, PlayableRoom::getNorthId, PlayableRoom::setNorth,
                PlayableRoom.class);
        setRoomsDirection(allRooms, PlayableRoom::getSouthId, PlayableRoom::setSouth,
                PlayableRoom.class);
        setRoomsDirection(allRooms, PlayableRoom::getNorthEastId, PlayableRoom::setNorthEast,
                PlayableRoom.class);
        setRoomsDirection(allRooms, PlayableRoom::getNorthWestId, PlayableRoom::setNorthWest,
                PlayableRoom.class);
        setRoomsDirection(allRooms, PlayableRoom::getSouthEastId, PlayableRoom::setSouthEast,
                PlayableRoom.class);
        setRoomsDirection(allRooms, PlayableRoom::getSouthWestId, PlayableRoom::setSouthWest,
                PlayableRoom.class);
        setRoomsDirection(allRooms, PlayableRoom::getUpId, PlayableRoom::setUp,
                PlayableRoom.class);
        setRoomsDirection(allRooms, PlayableRoom::getDownId, PlayableRoom::setDown,
                PlayableRoom.class);

        setRoomsDirection(allRooms, CutsceneRoom::getNextRoomId, CutsceneRoom::setNextRoom,
                CutsceneRoom.class);
    }

    private <T extends AbstractRoom> void setRoomsDirection(List<AbstractRoom> rooms,
            Function<T, Integer> directionIdGetter, BiConsumer<T, AbstractRoom> directionSetter,
            Class<T> clazz) {
        rooms.stream()
                .filter(clazz::isInstance)
                .map(clazz::cast)
                .filter(room -> directionIdGetter.apply(room) != null)
                .forEach(room -> rooms.stream()
                        .filter(linkedRoom -> linkedRoom.getId() == directionIdGetter.apply(room))
                        .forEach(linkedRoom -> directionSetter.accept(room, linkedRoom)));
    }

    private void moveTo(AbstractRoom room) {
        PlayableRoom currentRoom = (PlayableRoom) getCurrentRoom();

        if (!currentRoom.isCurrentlyDark()) {
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
