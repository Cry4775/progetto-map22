package engine;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import component.entity.AbstractEntity;
import component.entity.Entities;
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
import component.room.CutsceneRoom;
import component.room.PlayableRoom;
import component.room.Rooms;
import engine.command.Command;
import engine.command.CommandType;
import engine.database.DBManager;
import engine.loader.CommandsLoader;
import engine.loader.RoomsLoader;
import engine.loader.RoomsLoader.Mode;
import engine.parser.ParserResult;
import gui.GUIManager;

public class GameManager {
    private static GameManager instance;

    private final List<AbstractRoom> rooms = new ArrayList<>();
    private final List<Command> commands = new ArrayList<>();
    private final Inventory inventory = new Inventory();

    private AbstractRoom currentRoom;
    private AbstractRoom previousRoom;

    private static Status status = new Status();

    private GameManager() {}

    protected void initialize() {
        CommandsLoader commandsLoader = new CommandsLoader(commands);
        Thread tCommands = new Thread(commandsLoader, "CommandsLoader");

        RoomsLoader roomsLoader;

        if (DBManager.existsSaving()) {
            roomsLoader = new RoomsLoader(this,
                    GUIManager.askLoadingConfirmation() == JOptionPane.YES_OPTION ? Mode.DB : Mode.JSON);
        } else {
            roomsLoader = new RoomsLoader(this, Mode.JSON);
        }

        Thread tRooms = new Thread(roomsLoader, "RoomsLoader");

        tCommands.start();
        tRooms.start();

        try {
            tCommands.join();
            tRooms.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected static synchronized GameManager getInstance() {
        if (instance == null) {
            instance = new GameManager();
        }

        return instance;
    }

    public void addRoom(AbstractRoom room) {
        rooms.add(room);
    }

    public void addRooms(List<AbstractRoom> rooms) {
        this.rooms.addAll(rooms);
    }

    public List<AbstractRoom> getRooms() {
        return rooms;
    }

    public static Status getStatus() {
        return status;
    }

    public AbstractRoom getPreviousRoom() {
        return previousRoom;
    }

    public void setPreviousRoom(AbstractRoom previousRoom) {
        this.previousRoom = previousRoom;
    }

    public List<Command> getCommands() {
        return commands;
    }

    public AbstractRoom getCurrentRoom() {
        return currentRoom;
    }

    public void setCurrentRoom(AbstractRoom currentRoom) {
        this.currentRoom = currentRoom;
    }

    public Inventory getInventory() {
        return inventory;
    }

    void nextMove(ParserResult p) {
        PlayableRoom currentPlayableRoom = (PlayableRoom) getCurrentRoom();
        boolean actionPerformed = false;

        // TODO controlla tutti i getList e il check != null altrimenti da nullPointer
        CommandType commandType = p.getCommand().getType();

        if (isMovementCommand(commandType)) {
            if (isMovementGranted(commandType)) {
                actionPerformed = moveTo(currentPlayableRoom.getRoomAt(commandType));
            }
        } else {
            AbstractEntity roomObj = p.getRoomObject();
            AbstractEntity invObj = p.getInvObject();

            AbstractEntity anyObj = p.getObject();

            switch (commandType) {
                case SAVE: {
                    DBManager.save(this);

                    GUIManager.appendOutput("Partita salvata correttamente!");
                    break;
                }
                case INVENTORY: {
                    if (!inventory.isEmpty()) {
                        StringBuilder stringBuilder = new StringBuilder("Nel tuo inventario ci sono:");

                        for (AbstractEntity obj : inventory.getObjects()) {
                            stringBuilder.append("\n - " + obj.getName());
                            if (obj instanceof IWearable) {
                                IWearable wearable = (IWearable) obj;

                                stringBuilder.append(wearable.isWorn() ? " (INDOSSATO)" : "");
                            }
                        }

                        GUIManager.appendOutput(stringBuilder);
                    } else {
                        GUIManager.appendOutput("Il tuo inventario è vuoto.");
                    }

                    break;
                }

                case LOOK_AT: {
                    if (anyObj != null) {
                        anyObj.lookAt();
                    } else {
                        GUIManager.appendOutput("Non trovo cosa esaminare.");
                    }

                    break;
                }
                case PICK_UP: {
                    if (anyObj != null) {
                        if (anyObj instanceof IPickupable) {
                            IPickupable pickupObj = (IPickupable) anyObj;

                            actionPerformed = pickupObj.pickup(inventory);
                        } else {
                            GUIManager.appendOutput("Non puoi raccogliere questo oggetto.");
                        }
                    } else {
                        GUIManager.appendOutput("Non trovo cosa raccogliere.");
                    }

                    break;
                }
                case OPEN: {
                    if (anyObj != null) {
                        if (anyObj instanceof IOpenable) {
                            IOpenable openableObj = (IOpenable) anyObj;

                            actionPerformed = openableObj.open(invObj);
                        } else if (anyObj instanceof UnopenableDoor) {
                            UnopenableDoor fakeDoor = (UnopenableDoor) anyObj;

                            GUIManager.appendOutput(fakeDoor.getOpenEventText());
                        } else {
                            GUIManager.appendOutput("Non puoi aprire " + anyObj.getName());
                        }
                    } else {
                        GUIManager.appendOutput("Non trovo cosa aprire.");
                    }

                    break;
                }
                case PUSH: {
                    if (anyObj != null) {
                        if (anyObj instanceof IPushable) {
                            IPushable pushableObj = (IPushable) anyObj;

                            actionPerformed = pushableObj.push();
                        } else {
                            GUIManager.appendOutput("Non puoi premere " + anyObj.getName());
                        }
                    } else {
                        GUIManager.appendOutput("Non trovo cosa premere.");
                    }

                    break;
                }
                case PULL: {
                    if (anyObj != null) {
                        if (anyObj instanceof IPullable) {
                            IPullable pullableObj = (IPullable) anyObj;

                            actionPerformed = pullableObj.pull();
                        } else {
                            GUIManager.appendOutput("Non puoi tirare " + anyObj.getName());
                        }
                    } else {
                        GUIManager.appendOutput("Non trovo cosa tirare.");
                    }

                    break;
                }
                case MOVE: {
                    if (roomObj != null) {
                        if (roomObj instanceof IMovable) {
                            IMovable movableObj = (IMovable) roomObj;

                            actionPerformed = movableObj.move();
                        } else {
                            GUIManager.appendOutput("Non puoi spostare " + roomObj.getName());
                        }
                    } else {
                        GUIManager.appendOutput("Non trovo l'oggetto da spostare.");
                    }

                    break;
                }
                case INSERT: {
                    if (roomObj != null && invObj != null) {
                        if (roomObj instanceof AbstractContainer) {
                            AbstractContainer container = (AbstractContainer) roomObj;

                            actionPerformed = container.insert(invObj, inventory);
                        } else {
                            GUIManager.appendOutput("Non puoi farlo.");
                        }
                    } else if (roomObj == null && invObj != null) {
                        GUIManager.appendOutput("Non ho capito dove inserire.");
                    } else if (roomObj != null && invObj == null) {
                        GUIManager.appendOutput("Non ho capito cosa inserire.");
                    } else {
                        GUIManager.appendOutput("Non ho capito cosa devo fare.");
                    }

                    break;
                }
                case WEAR: {
                    if (roomObj != null) {
                        if (roomObj instanceof IWearable) {
                            GUIManager.appendOutput("Devi prima prenderlo per poterlo indossare.");
                        } else {
                            GUIManager.appendOutput("Non puoi indossarlo.");
                        }
                    } else if (invObj != null) {
                        if (invObj instanceof IWearable) {
                            IWearable wearable = (IWearable) invObj;

                            actionPerformed = wearable.wear();
                        } else {
                            GUIManager.appendOutput("Non puoi indossarlo.");
                        }
                    } else {
                        GUIManager.appendOutput("Non trovo l'oggetto da indossare.");
                    }

                    break;
                }
                case UNWEAR: {
                    if (roomObj != null) {
                        GUIManager.appendOutput("Non posso togliermi qualcosa che non ho addosso.");
                    } else if (invObj != null) {
                        if (invObj instanceof IWearable) {
                            IWearable wearable = (IWearable) invObj;

                            actionPerformed = wearable.unwear();
                        } else {
                            GUIManager.appendOutput("Non puoi farlo.");
                        }
                    } else {
                        GUIManager.appendOutput("Non trovo l'oggetto da togliere.");
                    }

                    break;
                }
                case TURN_ON: {
                    if (anyObj != null) {
                        if (anyObj instanceof ISwitch) {
                            ISwitch switchObj = (ISwitch) anyObj;

                            actionPerformed = switchObj.turnOn();
                        } else {
                            GUIManager.appendOutput("Non puoi accenderlo.");
                        }
                    } else {
                        GUIManager.appendOutput("Non trovo l'oggetto da accendere.");
                    }

                    break;
                }
                case TURN_OFF: {
                    if (anyObj != null) {
                        if (anyObj instanceof ISwitch) {
                            ISwitch switchObj = (ISwitch) anyObj;

                            actionPerformed = switchObj.turnOff();
                        } else if (anyObj instanceof FireObject) {
                            FireObject fire = (FireObject) anyObj;

                            if (invObj instanceof IFluid) {
                                IFluid fluid = (IFluid) invObj;

                                actionPerformed = fire.extinguish(fluid);
                            } else if (invObj != null) {
                                GUIManager.appendOutput("Non puoi spegnerlo con quello. ");
                            } else {
                                GUIManager.appendOutput("Non puoi spegnerlo senza qualcosa di adatto.");
                            }
                        } else {
                            GUIManager.appendOutput("Non puoi spegnerlo.");
                        }
                    } else {
                        GUIManager.appendOutput("Non trovo l'oggetto da spegnere.");
                    }

                    break;
                }
                case TALK_TO: {
                    if (roomObj != null) {
                        if (roomObj instanceof ITalkable) {
                            ITalkable talkableObj = (ITalkable) roomObj;

                            actionPerformed = talkableObj.talk();
                        } else {
                            GUIManager.appendOutput("Non puoi parlarci.");
                        }
                    } else {
                        GUIManager.appendOutput("Non trovo con chi parlare.");
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

                                    actionPerformed = fire.extinguish(fluid);
                                } else if (roomObj instanceof AbstractContainer) {
                                    AbstractContainer container = (AbstractContainer) roomObj;

                                    actionPerformed = container.insert(invObj, inventory);
                                } else {
                                    GUIManager.appendOutput("Non puoi versarci il liquido.");
                                }
                            } else {
                                GUIManager.appendOutput("Non trovo dove versare il liquido.");
                            }
                        } else {
                            GUIManager.appendOutput("Non posso versare qualcosa che non sia liquido.");
                        }
                    } else {
                        GUIManager.appendOutput("Non trovo cosa versare.");
                    }

                    break;
                }
                case READ: {
                    if (anyObj != null) {
                        if (anyObj instanceof IReadable) {
                            IReadable readableObj = (IReadable) anyObj;

                            actionPerformed = readableObj.read();
                        } else {
                            GUIManager.appendOutput("Non posso leggerlo.");
                        }
                    } else {
                        GUIManager.appendOutput("Non trovo cosa leggere.");
                    }

                    break;
                }
                default:
                    break;
            }
        }

        if (getCurrentRoom() instanceof PlayableRoom) {
            currentPlayableRoom = (PlayableRoom) getCurrentRoom();
            currentPlayableRoom.processRoomLighting(inventory.getObjects());
        } else {
            currentPlayableRoom = null;
        }

        if (actionPerformed) {
            GUIManager.increaseActionsCounter();
            inventory.checkForDestroyableItems();
        }

        if (status.isMovementAttempt()) {
            if (!status.isPositionChanged() && currentPlayableRoom != null && currentPlayableRoom.isCurrentlyDark()) {
                GUIManager.appendOutput("Meglio non avventurarsi nel buio.");
            } else if (status.isRoomBlockedByDoor()) {
                GUIManager.appendOutput("La porta é chiusa.");
            } else if (status.isRoomBlockedByWall()) {
                GUIManager.appendOutput(status.getWall().getTrespassingWhenLockedText());
            } else if (status.isPositionChanged()) {
                if (currentPlayableRoom != null && !currentPlayableRoom.isCurrentlyDark()) {
                    GUIManager.appendOutput(PlayableRoom.processRoomEvent(getCurrentRoom()));
                }
            } else {
                GUIManager.appendOutput("Da quella parte non si puó andare.");
            }
        }

        if (status.isWarp()) {
            setPreviousRoom(getCurrentRoom());
            setCurrentRoom(status.getWarpDestination());

            GUIManager.appendOutput(PlayableRoom.processRoomEvent(getCurrentRoom()));
        }

        status.reset();
    }

    void nextRoom() {
        if (getCurrentRoom() instanceof CutsceneRoom) {
            CutsceneRoom currentRoom = (CutsceneRoom) getCurrentRoom();

            if (currentRoom.getNextRoom() != null) {
                setCurrentRoom(currentRoom.getNextRoom());
            } else {
                throw new Error("Couldn't find the next room of " + currentRoom.getName()
                        + " (" + currentRoom.getId() + "). Check the JSON file for correct room setup.");
            }
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

    private boolean moveTo(AbstractRoom room) {
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
                                return true;
                            } else if (door.getBlockedRoomId().equals(room.getId()) && !door.isOpen()) {
                                status.setPositionChanged(false, true);
                                return false;
                            }
                        }
                    }
                }
                setPreviousRoom(currentRoom);
                setCurrentRoom(room);
                status.setPositionChanged(true);
                return true;
            } else {
                status.setPositionChanged(false);
                return false;
            }
        } else {
            if (room != null) {
                if (room.equals(getPreviousRoom())) {
                    setPreviousRoom(currentRoom);
                    setCurrentRoom(room);
                    status.setPositionChanged(true);
                    return true;
                }
            } else {
                status.setPositionChanged(false);
                return false;
            }
        }

        return false;
    }

    public List<AbstractRoom> listAllRooms() {
        List<AbstractRoom> result = new ArrayList<>();
        result.addAll(Rooms.listAllRooms(rooms));

        return result;
    }

    public Multimap<String, AbstractEntity> mapAllRoomsObjects() {
        Multimap<String, AbstractEntity> result = ArrayListMultimap.create();
        result.putAll(Entities.mapRoomsObjects(rooms));

        return result;
    }
}
