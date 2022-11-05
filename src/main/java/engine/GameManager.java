package engine;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
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
import component.entity.interfaces.ILightSource;
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
import component.event.ObjectEvent;
import component.event.RoomEvent;
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

    private static AbstractRoom currentRoom;

    private static AbstractRoom previousRoom;

    private static Status status = new Status();

    private static StringBuilder outString;

    public Status getStatus() {
        return status;
    }

    public static AbstractRoom getPreviousRoom() {
        return previousRoom;
    }

    public static void setPreviousRoom(AbstractRoom previousRoom) {
        GameManager.previousRoom = previousRoom;
    }

    public List<AbstractRoom> getRooms() {
        return rooms;
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

    public static List<AbstractEntity> getInventory() {
        return inventory;
    }

    public static List<AbstractEntity> getFullInventory() {
        List<AbstractEntity> inv = new ArrayList<>();

        for (AbstractEntity obj : inventory) {
            inv.add(obj);

            inv.addAll(AbstractContainer.getAllObjectsInside(obj));
        }

        return inv;
    }

    public void setCompassLabel(AbstractRoom room, JLabel directionLbl) {
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

    public void init() throws IOException {
        outString = new StringBuilder();

        CommandsLoader commandsLoader = new CommandsLoader();
        Thread tCommands = new Thread(commandsLoader, "CommandsLoader");

        RoomsLoader roomsLoader;

        if (DBManager.existsSaving()) {
            roomsLoader = new RoomsLoader(getRooms(),
                    MainFrame.askLoadingConfirmation() == JOptionPane.YES_OPTION ? Mode.DB
                            : Mode.JSON);
        } else {
            roomsLoader = new RoomsLoader(getRooms(), Mode.JSON);
        }

        Thread tRooms = new Thread(roomsLoader, "RoomsLoader");

        tCommands.start();
        tRooms.start();

        try {
            tCommands.join();
            tRooms.join();
            if (currentRoom instanceof PlayableRoom) {
                processRoomLighting((PlayableRoom) currentRoom);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean isMovementGranted(CommandType direction) {
        PlayableRoom currentRoom = (PlayableRoom) getCurrentRoom();

        getStatus().setMovementAttempt(true);

        InvisibleWall wall = currentRoom.getMagicWall(direction);
        if (wall != null) {
            getStatus().setRoomBlockedByWall(true);
            getStatus().setWall(wall);
            return false;
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

                    outString.append("Partita salvata correttamente!");
                    break;
                }
                case INVENTORY: {
                    if (!getInventory().isEmpty()) {
                        outString.append("Nel tuo inventario ci sono:");

                        for (AbstractEntity obj : getInventory()) {
                            outString.append("\n - " + obj.getName());
                            if (obj instanceof IWearable) {
                                IWearable wearable = (IWearable) obj;

                                outString.append(wearable.isWorn() ? " (INDOSSATO)" : "");
                            }
                        }
                    } else {
                        outString.append("Il tuo inventario è vuoto.");
                    }

                    break;
                }

                case LOOK_AT: {
                    if (anyObj != null) {
                        outString.append(anyObj.getLookMessage());
                    } else {
                        outString.append("Non trovo cosa esaminare.");
                    }

                    break;
                }
                case PICK_UP: {
                    if (anyObj != null) {
                        if (anyObj instanceof IPickupable) {
                            IPickupable pickupObj = (IPickupable) anyObj;

                            outString.append(pickupObj.pickup());
                        } else {
                            outString.append("Non puoi raccogliere questo oggetto.");
                        }
                    } else {
                        outString.append("Non trovo cosa raccogliere.");
                    }

                    break;
                }
                case OPEN: {
                    if (anyObj != null) {
                        if (anyObj instanceof IOpenable) {
                            IOpenable openableObj = (IOpenable) anyObj;

                            outString.append(openableObj.open(invObj));
                        } else if (anyObj instanceof UnopenableDoor) {
                            UnopenableDoor fakeDoor = (UnopenableDoor) anyObj;

                            outString.append(fakeDoor.getOpenEventText());
                        } else {
                            outString.append("Non puoi aprire " + anyObj.getName());
                        }
                    } else {
                        outString.append("Non trovo cosa aprire.");
                    }

                    break;
                }
                case PUSH: {
                    if (anyObj != null) {
                        if (anyObj instanceof IPushable) {
                            IPushable pushableObj = (IPushable) anyObj;

                            outString.append(pushableObj.push());
                        } else {
                            outString.append("Non puoi premere " + anyObj.getName());
                        }
                    } else {
                        outString.append("Non trovo cosa premere.");
                    }

                    break;
                }
                case PULL: {
                    if (anyObj != null) {
                        if (anyObj instanceof IPullable) {
                            IPullable pullableObj = (IPullable) anyObj;

                            outString.append(pullableObj.pull());
                        } else {
                            outString.append("Non puoi tirare " + anyObj.getName());
                        }
                    } else {
                        outString.append("Non trovo cosa tirare.");
                    }

                    break;
                }
                case MOVE: {
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

                    break;
                }
                case INSERT: {
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

                    break;
                }
                case WEAR: {
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

                    break;
                }
                case UNWEAR: {
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

                    break;
                }
                case TURN_ON: {
                    if (anyObj != null) {
                        if (anyObj instanceof ISwitch) {
                            ISwitch switchObj = (ISwitch) anyObj;

                            outString.append(switchObj.turnOn());
                        } else {
                            outString.append("Non puoi accenderlo.");
                        }
                    } else {
                        outString.append("Non trovo l'oggetto da accendere.");
                    }

                    break;
                }
                case TURN_OFF: {
                    if (anyObj != null) {
                        if (anyObj instanceof ISwitch) {
                            ISwitch switchObj = (ISwitch) anyObj;

                            outString.append(switchObj.turnOff());
                        } else if (anyObj instanceof FireObject) {
                            FireObject fire = (FireObject) anyObj;

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

                    break;
                }
                case TALK_TO: {
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

                    break;
                }
                case POUR: {
                    if (invObj != null) {
                        if (invObj instanceof IFluid) {
                            if (roomObj != null) {
                                if (roomObj instanceof FireObject) {
                                    IFluid fluid = (IFluid) invObj;
                                    FireObject fire = (FireObject) roomObj;

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

                    break;
                }
                case READ: {
                    if (anyObj != null) {
                        if (anyObj instanceof IReadable) {
                            IReadable readableObj = (IReadable) anyObj;

                            outString.append(readableObj.read());
                        } else {
                            outString.append("Non posso leggerlo.");
                        }
                    } else {
                        outString.append("Non trovo cosa leggere.");
                    }

                    break;
                }
                default:
                    break;
            }

            processTriggeredEvents(roomObj);
            processTriggeredEvents(invObj);
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
            processRoomLighting(currentPlayableRoom);
        } else {
            currentPlayableRoom = null;
        }

        if (getStatus().isMovementAttempt()) {
            if (!getStatus().isPositionChanged() && currentPlayableRoom != null
                    && currentPlayableRoom.isCurrentlyDark()) {
                outString.append("Meglio non avventurarsi nel buio.");
            } else if (getStatus().isRoomBlockedByDoor()) {
                outString.append("La porta é chiusa.");
            } else if (getStatus().isRoomBlockedByWall()) {
                outString.append(getStatus().getWall().getTrespassingWhenLockedText());
            } else if (getStatus().isPositionChanged()) {
                if (currentPlayableRoom != null && currentPlayableRoom.isCurrentlyDark()) {
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

            outString.append(outString.length() > 0 ? "\n\n" : "");
            outString.append(getCurrentRoom().getDescription());
            outString.append(handleRoomEvent());
        }

        if (outString.length() > 0) {
            gui.appendText(outString.toString());
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
                Iterator<ObjectEvent> it = obj.getEvents().iterator();

                while (it.hasNext()) {
                    ObjectEvent evt = it.next();

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
                        outString.append("\n\n" + evt.getText());
                    }

                    evt.setTriggered(true);

                    return outString.toString();
                }
            }
        }
        return "";
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
                                getStatus().setPositionChanged(true);
                                return;
                            } else if (door.getBlockedRoomId().equals(room.getId())
                                    && !door.isOpen()) {
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

    public static List<AbstractRoom> listAllRooms() {
        List<AbstractRoom> result = new ArrayList<>();

        for (AbstractRoom room : rooms) {
            result.addAll(AbstractRoom.getAllRooms(room));
        }

        return result;
    }

    public static List<AbstractRoom> listAllRooms(List<AbstractRoom> rooms) {
        List<AbstractRoom> result = new ArrayList<>();

        for (AbstractRoom room : rooms) {
            result.addAll(AbstractRoom.getAllRooms(room));
        }

        return result;
    }

    public static Multimap<String, AbstractEntity> mapAllObjects() {
        Multimap<String, AbstractEntity> result = ArrayListMultimap.create();

        for (AbstractRoom room : rooms) {
            if (room instanceof PlayableRoom) {
                PlayableRoom pRoom = (PlayableRoom) room;
                result.putAll(pRoom.getObjectsAsMap(PlayableRoom.Mode.INCLUDE_EVERYTHING));
            }
        }

        return result;
    }

    public static Multimap<String, AbstractEntity> mapAllObjects(List<AbstractRoom> rooms) {
        Multimap<String, AbstractEntity> result = ArrayListMultimap.create();

        for (AbstractRoom room : rooms) {
            if (room instanceof PlayableRoom) {
                PlayableRoom pRoom = (PlayableRoom) room;
                result.putAll(pRoom.getObjectsAsMap(PlayableRoom.Mode.INCLUDE_EVERYTHING));
            }
        }

        return result;
    }

}
