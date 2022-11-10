package engine;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import component.entity.AbstractEntity;
import component.entity.container.AbstractContainer;
import component.entity.doorlike.Door;
import component.entity.doorlike.InvisibleWall;
import component.entity.doorlike.UnopenableDoor;
import component.entity.interfaces.IFluid;
import component.entity.interfaces.IMovable;
import component.entity.interfaces.IOpenable;
import component.entity.interfaces.IPickupable;
import component.entity.interfaces.IPullable;
import component.entity.interfaces.IPushable;
import component.entity.interfaces.IReadable;
import component.entity.interfaces.ISwitch;
import component.entity.interfaces.ITalkable;
import component.entity.interfaces.IWearable;
import component.entity.object.FireObject;
import component.room.AbstractRoom;
import component.room.PlayableRoom;
import engine.command.Command;
import engine.command.CommandType;
import engine.database.DBManager;
import engine.loader.CommandsLoader;
import engine.loader.RoomsLoader;
import engine.loader.RoomsLoader.Mode;
import engine.parser.ParserOutput;
import gui.MainFrame;

public class GameManager {

    private static final List<AbstractRoom> rooms = new ArrayList<>();
    private static final List<Command> commands = new ArrayList<>();
    private static final List<AbstractEntity> inventory = new ArrayList<>();

    private static final List<AbstractRoom> allRooms = new ArrayList<>();
    private static final Multimap<String, AbstractEntity> allRoomsObjects =
            ArrayListMultimap.create();

    private static AbstractRoom currentRoom;
    private static AbstractRoom previousRoom;

    private static Status status = new Status();

    public static Status getStatus() {
        return status;
    }

    public static AbstractRoom getPreviousRoom() {
        return previousRoom;
    }

    public static void setPreviousRoom(AbstractRoom previousRoom) {
        GameManager.previousRoom = previousRoom;
    }

    public static List<Command> getCommands() {
        return commands;
    }

    public static AbstractRoom getCurrentRoom() {
        return currentRoom;
    }

    public static void setCurrentRoom(AbstractRoom currentRoom) {
        GameManager.currentRoom = currentRoom;
    }

    public enum InventoryMode {
        NORMAL,
        UNPACK_CONTAINERS
    }

    public static List<AbstractEntity> getInventory() {
        return inventory;
    }

    public static List<AbstractEntity> getInventory(InventoryMode mode) {
        switch (mode) {
            case NORMAL:
                return inventory;
            case UNPACK_CONTAINERS:
                List<AbstractEntity> result = new ArrayList<>();

                for (AbstractEntity obj : inventory) {
                    result.add(obj);
                    result.addAll(AbstractContainer.getAllObjectsInside(obj));
                }

                return result;
            default:
                return inventory;
        }
    }

    private void clearCompassLabels(MainFrame gui) {
        setCompassLabel(null, gui.getLblCompassNorthText());
        setCompassLabel(null, gui.getLblCompassSouthText());
        setCompassLabel(null, gui.getLblCompassWestText());
        setCompassLabel(null, gui.getLblCompassEastText());
        setCompassLabel(null, gui.getLblCompassSouthWestText());
        setCompassLabel(null, gui.getLblCompassSouthEastText());
        setCompassLabel(null, gui.getLblCompassNorthEastText());
        setCompassLabel(null, gui.getLblCompassNorthWestText());
    }

    public void setCompassLabels(MainFrame gui) {
        if (getCurrentRoom() instanceof PlayableRoom) {
            PlayableRoom currentPlayableRoom = (PlayableRoom) getCurrentRoom();
            if (!currentPlayableRoom.isCurrentlyDark()) {
                setCompassLabel(currentPlayableRoom.getNorth(), gui.getLblCompassNorthText());
                setCompassLabel(currentPlayableRoom.getSouth(), gui.getLblCompassSouthText());
                setCompassLabel(currentPlayableRoom.getWest(), gui.getLblCompassWestText());
                setCompassLabel(currentPlayableRoom.getEast(), gui.getLblCompassEastText());
                setCompassLabel(currentPlayableRoom.getSouthWest(),
                        gui.getLblCompassSouthWestText());
                setCompassLabel(currentPlayableRoom.getSouthEast(),
                        gui.getLblCompassSouthEastText());
                setCompassLabel(currentPlayableRoom.getNorthEast(),
                        gui.getLblCompassNorthEastText());
                setCompassLabel(currentPlayableRoom.getNorthWest(),
                        gui.getLblCompassNorthWestText());
            } else {
                clearCompassLabels(gui);

                if (previousRoom.equals(currentPlayableRoom.getSouth())) {
                    setCompassLabel(currentPlayableRoom.getSouth(), gui.getLblCompassSouthText());
                } else if (previousRoom.equals(currentPlayableRoom.getNorth())) {
                    setCompassLabel(currentPlayableRoom.getNorth(), gui.getLblCompassNorthText());
                } else if (previousRoom.equals(currentPlayableRoom.getEast())) {
                    setCompassLabel(currentPlayableRoom.getEast(), gui.getLblCompassEastText());
                } else if (previousRoom.equals(currentPlayableRoom.getWest())) {
                    setCompassLabel(currentPlayableRoom.getWest(), gui.getLblCompassWestText());
                } else if (previousRoom.equals(currentPlayableRoom.getNorthWest())) {
                    setCompassLabel(currentPlayableRoom.getNorthWest(),
                            gui.getLblCompassNorthWestText());
                } else if (previousRoom.equals(currentPlayableRoom.getNorthEast())) {
                    setCompassLabel(currentPlayableRoom.getNorthEast(),
                            gui.getLblCompassNorthEastText());
                } else if (previousRoom.equals(currentPlayableRoom.getSouthWest())) {
                    setCompassLabel(currentPlayableRoom.getSouthWest(),
                            gui.getLblCompassSouthWestText());
                } else if (previousRoom.equals(currentPlayableRoom.getSouthEast())) {
                    setCompassLabel(currentPlayableRoom.getSouthEast(),
                            gui.getLblCompassSouthEastText());
                }
            }
        } else {
            clearCompassLabels(gui);
        }
    }

    private void setCompassLabel(AbstractRoom room, JLabel directionLbl) {
        if (room != null) {
            if (room.equals(previousRoom)) {
                directionLbl.setForeground(Color.BLUE);
            } else {
                directionLbl.setForeground(Color.GREEN);
            }

            if (getCurrentRoom() instanceof PlayableRoom) {
                PlayableRoom playableRoom = (PlayableRoom) getCurrentRoom();
                if (playableRoom.getObjects() != null) {
                    for (AbstractEntity obj : playableRoom.getObjects()) {
                        if (obj instanceof Door) {
                            Door door = (Door) obj;
                            if (door.getBlockedRoomId().equals(room.getId()) && !door.isOpen()) {
                                directionLbl.setForeground(Color.ORANGE);
                                break;
                            }
                        }
                    }
                }
            }
        } else {
            directionLbl.setForeground(Color.RED);
        }
    }

    public void init() throws IOException {
        CommandsLoader commandsLoader = new CommandsLoader();
        Thread tCommands = new Thread(commandsLoader, "CommandsLoader");

        RoomsLoader roomsLoader;

        if (DBManager.existsSaving()) {
            roomsLoader = new RoomsLoader(rooms,
                    MainFrame.askLoadingConfirmation() == JOptionPane.YES_OPTION ? Mode.DB
                            : Mode.JSON);
        } else {
            roomsLoader = new RoomsLoader(rooms, Mode.JSON);
        }

        Thread tRooms = new Thread(roomsLoader, "RoomsLoader");

        tCommands.start();
        tRooms.start();

        try {
            tCommands.join();
            tRooms.join();
            if (currentRoom instanceof PlayableRoom) {
                ((PlayableRoom) currentRoom).processRoomLighting(inventory);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private boolean isMovementGranted(CommandType direction) {
        PlayableRoom currentRoom = (PlayableRoom) getCurrentRoom();

        status.setMovementAttempt(true);

        InvisibleWall wall = currentRoom.getMagicWall(direction);
        if (wall != null) {
            status.setRoomBlockedByWall(true);
            status.setWall(wall);
            return false;
        }

        return true;
    }

    private boolean isMovementCommand(CommandType commandType) {
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

    public void nextMove(ParserOutput p, MainFrame gui) {
        PlayableRoom currentPlayableRoom = (PlayableRoom) getCurrentRoom();

        // TODO controlla tutti i getList e il check != null altrimenti da nullPointer
        CommandType commandType = p.getCommand().getType();

        if (isMovementCommand(commandType)) {
            if (isMovementGranted(commandType)) {
                moveTo(currentPlayableRoom.getRoomAt(commandType));
            }
        } else {

            AbstractEntity roomObj = p.getObject();
            AbstractEntity invObj = p.getInvObject();

            AbstractEntity anyObj = getObjectFromParser(p);

            switch (commandType) {
                case SAVE: {
                    DBManager.save();

                    OutputManager.append("Partita salvata correttamente!");
                    break;
                }
                case INVENTORY: {
                    if (!getInventory().isEmpty()) {
                        StringBuilder stringBuilder = new StringBuilder(
                                "Nel tuo inventario ci sono:");

                        for (AbstractEntity obj : getInventory()) {
                            stringBuilder.append("\n - " + obj.getName());
                            if (obj instanceof IWearable) {
                                IWearable wearable = (IWearable) obj;

                                stringBuilder.append(wearable.isWorn() ? " (INDOSSATO)" : "");
                            }
                        }

                        OutputManager.append(stringBuilder);
                    } else {
                        OutputManager.append("Il tuo inventario è vuoto.");
                    }

                    break;
                }

                case LOOK_AT: {
                    if (anyObj != null) {
                        anyObj.sendLookMessage();
                    } else {
                        OutputManager.append("Non trovo cosa esaminare.");
                    }

                    break;
                }
                case PICK_UP: {
                    if (anyObj != null) {
                        if (anyObj instanceof IPickupable) {
                            IPickupable pickupObj = (IPickupable) anyObj;

                            pickupObj.pickup();
                        } else {
                            OutputManager.append("Non puoi raccogliere questo oggetto.");
                        }
                    } else {
                        OutputManager.append("Non trovo cosa raccogliere.");
                    }

                    break;
                }
                case OPEN: {
                    if (anyObj != null) {
                        if (anyObj instanceof IOpenable) {
                            IOpenable openableObj = (IOpenable) anyObj;

                            openableObj.open(invObj);
                        } else if (anyObj instanceof UnopenableDoor) {
                            UnopenableDoor fakeDoor = (UnopenableDoor) anyObj;

                            OutputManager.append(fakeDoor.getOpenEventText());
                        } else {
                            OutputManager.append("Non puoi aprire " + anyObj.getName());
                        }
                    } else {
                        OutputManager.append("Non trovo cosa aprire.");
                    }

                    break;
                }
                case PUSH: {
                    if (anyObj != null) {
                        if (anyObj instanceof IPushable) {
                            IPushable pushableObj = (IPushable) anyObj;

                            pushableObj.push();
                        } else {
                            OutputManager.append("Non puoi premere " + anyObj.getName());
                        }
                    } else {
                        OutputManager.append("Non trovo cosa premere.");
                    }

                    break;
                }
                case PULL: {
                    if (anyObj != null) {
                        if (anyObj instanceof IPullable) {
                            IPullable pullableObj = (IPullable) anyObj;

                            pullableObj.pull();
                        } else {
                            OutputManager.append("Non puoi tirare " + anyObj.getName());
                        }
                    } else {
                        OutputManager.append("Non trovo cosa tirare.");
                    }

                    break;
                }
                case MOVE: {
                    if (roomObj != null) {
                        if (roomObj instanceof IMovable) {
                            IMovable movableObj = (IMovable) roomObj;

                            movableObj.move();
                        } else {
                            OutputManager.append("Non puoi spostare " + roomObj.getName());
                        }
                    } else {
                        OutputManager.append("Non trovo l'oggetto da spostare.");
                    }

                    break;
                }
                case INSERT: {
                    if (roomObj != null && invObj != null) {
                        if (roomObj instanceof AbstractContainer) {
                            AbstractContainer container = (AbstractContainer) roomObj;

                            container.insert(invObj, getInventory());
                        }
                    } else if (roomObj == null && invObj != null) {
                        OutputManager.append("Non ho capito dove inserire.");
                    } else if (roomObj != null && invObj == null) {
                        OutputManager.append("Non ho capito cosa inserire.");
                    } else {
                        OutputManager.append("Non ho capito cosa devo fare.");
                    }

                    break;
                }
                case WEAR: {
                    if (roomObj != null) {
                        if (roomObj instanceof IWearable) {
                            OutputManager.append("Devi prima prenderlo per poterlo indossare.");
                        } else {
                            OutputManager.append("Non puoi indossarlo.");
                        }
                    } else if (invObj != null) {
                        if (invObj instanceof IWearable) {
                            IWearable wearable = (IWearable) invObj;

                            wearable.wear();
                        } else {
                            OutputManager.append("Non puoi indossarlo.");
                        }
                    } else {
                        OutputManager.append("Non trovo l'oggetto da indossare.");
                    }

                    break;
                }
                case UNWEAR: {
                    if (roomObj != null) {
                        OutputManager.append("Non posso togliermi qualcosa che non ho addosso.");
                    } else if (invObj != null) {
                        if (invObj instanceof IWearable) {
                            IWearable wearable = (IWearable) invObj;

                            wearable.unwear();
                        } else {
                            OutputManager.append("Non puoi farlo.");
                        }
                    } else {
                        OutputManager.append("Non trovo l'oggetto da togliere.");
                    }

                    break;
                }
                case TURN_ON: {
                    if (anyObj != null) {
                        if (anyObj instanceof ISwitch) {
                            ISwitch switchObj = (ISwitch) anyObj;

                            switchObj.turnOn();
                        } else {
                            OutputManager.append("Non puoi accenderlo.");
                        }
                    } else {
                        OutputManager.append("Non trovo l'oggetto da accendere.");
                    }

                    break;
                }
                case TURN_OFF: {
                    if (anyObj != null) {
                        if (anyObj instanceof ISwitch) {
                            ISwitch switchObj = (ISwitch) anyObj;

                            switchObj.turnOff();
                        } else if (anyObj instanceof FireObject) {
                            FireObject fire = (FireObject) anyObj;

                            if (invObj instanceof IFluid) {
                                IFluid fluid = (IFluid) invObj;

                                fire.extinguish(fluid);
                            } else if (invObj != null) {
                                OutputManager.append("Non puoi spegnerlo con quello. ");
                            } else {
                                OutputManager
                                        .append("Non puoi spegnerlo senza qualcosa di adatto.");
                            }
                        } else {
                            OutputManager.append("Non puoi spegnerlo.");
                        }
                    } else {
                        OutputManager.append("Non trovo l'oggetto da spegnere.");
                    }

                    break;
                }
                case TALK_TO: {
                    if (roomObj != null) {
                        if (roomObj instanceof ITalkable) {
                            ITalkable talkableObj = (ITalkable) roomObj;

                            talkableObj.talk();
                        } else {
                            OutputManager.append("Non puoi parlarci.");
                        }
                    } else {
                        OutputManager.append("Non trovo con chi parlare.");
                    }

                    break;
                }
                case POUR: {
                    if (invObj != null) {
                        if (invObj instanceof IFluid) {
                            if (roomObj != null) {
                                if (roomObj instanceof FireObject) {
                                    IFluid fluid = (IFluid) invObj;
                                    FireObject fire = (FireObject) roomObj;

                                    fire.extinguish(fluid);
                                } else if (roomObj instanceof AbstractContainer) {
                                    AbstractContainer container = (AbstractContainer) roomObj;

                                    container.insert(invObj, getInventory());
                                } else {
                                    OutputManager.append("Non puoi versarci il liquido.");
                                }
                            } else {
                                OutputManager.append("Non trovo dove versare il liquido.");
                            }
                        } else {
                            OutputManager.append("Non posso versare qualcosa che non sia liquido.");
                        }
                    } else {
                        OutputManager.append("Non trovo cosa versare.");
                    }

                    break;
                }
                case READ: {
                    if (anyObj != null) {
                        if (anyObj instanceof IReadable) {
                            IReadable readableObj = (IReadable) anyObj;

                            readableObj.read();
                        } else {
                            OutputManager.append("Non posso leggerlo.");
                        }
                    } else {
                        OutputManager.append("Non trovo cosa leggere.");
                    }

                    break;
                }
                default:
                    break;
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

        if (getCurrentRoom() instanceof PlayableRoom) {
            currentPlayableRoom = (PlayableRoom) getCurrentRoom();
            currentPlayableRoom.processRoomLighting(inventory);
        } else {
            currentPlayableRoom = null;
        }

        if (status.isMovementAttempt()) {
            if (!status.isPositionChanged() && currentPlayableRoom != null
                    && currentPlayableRoom.isCurrentlyDark()) {
                OutputManager.append("Meglio non avventurarsi nel buio.");
            } else if (status.isRoomBlockedByDoor()) {
                OutputManager.append("La porta é chiusa.");
            } else if (status.isRoomBlockedByWall()) {
                OutputManager.append(status.getWall().getTrespassingWhenLockedText());
            } else if (status.isPositionChanged()) {
                if (currentPlayableRoom != null && currentPlayableRoom.isCurrentlyDark()) {
                    OutputManager.append("È completamente buio e non riesci a vedere niente.");
                } else {
                    OutputManager.append(getCurrentRoom().getDescription());
                    OutputManager.append(PlayableRoom.processRoomEvent(getCurrentRoom()));
                }
            } else {
                OutputManager.append("Da quella parte non si puó andare.");
            }
        }

        if (status.isWarp()) {
            setPreviousRoom(getCurrentRoom());
            setCurrentRoom(status.getWarpDestination());

            OutputManager.append(getCurrentRoom().getDescription());
            OutputManager.append(PlayableRoom.processRoomEvent(getCurrentRoom()));
        }

        OutputManager.print();

        status.reset();
    }

    private boolean isActionPerformed(ParserOutput p) {
        if (status.isMovementAttempt() && status.isPositionChanged()) {
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

    private void moveTo(AbstractRoom room) {
        PlayableRoom currentRoom = (PlayableRoom) getCurrentRoom();

        if (!currentRoom.isCurrentlyDark()) {
            if (room != null) {
                if (currentRoom.getObjects() != null) {
                    for (AbstractEntity obj : currentRoom.getObjects()) {
                        if (obj instanceof Door) {
                            Door door = (Door) obj;
                            if (door.getBlockedRoomId().equals(room.getId()) && door.isOpen()) {
                                setPreviousRoom(currentRoom);
                                setCurrentRoom(room);
                                status.setPositionChanged(true);
                                return;
                            } else if (door.getBlockedRoomId().equals(room.getId())
                                    && !door.isOpen()) {
                                status.setPositionChanged(false, true);
                                return;
                            }
                        }
                    }
                }
                setPreviousRoom(currentRoom);
                setCurrentRoom(room);
                status.setPositionChanged(true);
                return;
            } else {
                status.setPositionChanged(false);
                return;
            }
        } else {
            if (room != null) {
                if (room.equals(getPreviousRoom())) {
                    setPreviousRoom(currentRoom);
                    setCurrentRoom(room);
                    status.setPositionChanged(true);
                    return;
                }
            } else {
                status.setPositionChanged(false);
            }
        }
    }

    public static List<AbstractRoom> listAllRooms() {
        if (allRooms.isEmpty()) {
            allRooms.addAll(listAllRooms(rooms));
        }

        return allRooms;
    }

    public static List<AbstractRoom> listAllRooms(List<AbstractRoom> rooms) {
        List<AbstractRoom> result = new ArrayList<>();

        for (AbstractRoom room : rooms) {
            result.addAll(AbstractRoom.getAllRooms(room));
        }

        return result;
    }

    public static Multimap<String, AbstractEntity> mapAllRoomsObjects() {
        if (allRoomsObjects.isEmpty()) {
            allRoomsObjects.putAll(mapAllRoomsObjects(rooms));
        } else {
            for (AbstractEntity obj : getInventory()) {
                allRoomsObjects.remove(obj.getId(), obj);
            }
        }

        return allRoomsObjects;
    }

    public static Multimap<String, AbstractEntity> mapAllRoomsObjects(List<AbstractRoom> rooms) {
        Multimap<String, AbstractEntity> result = ArrayListMultimap.create();

        for (AbstractRoom room : rooms) {
            if (room instanceof PlayableRoom) {
                PlayableRoom pRoom = (PlayableRoom) room;

                result.putAll(pRoom.getObjectsAsMap(PlayableRoom.Mode.INCLUDE_EVERYTHING));
            }
        }

        return result;
    }

    public static Multimap<String, AbstractEntity> mapAllInventoryObjects() {
        Multimap<String, AbstractEntity> result = ArrayListMultimap.create();

        for (AbstractEntity obj : getInventory(InventoryMode.UNPACK_CONTAINERS)) {
            result.put(obj.getId(), obj);
        }

        return result;
    }

}
