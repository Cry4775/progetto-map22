package engine;

import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
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
import engine.command.CommandType;
import engine.database.DBManager;
import engine.parser.Parser;
import engine.parser.ParserResult;
import gui.GUIManager;
import gui.MainFrame;
import rest.WeatherFetcher;
import sound.SoundManager;
import sound.SoundManager.Mode;
import utility.Utils;

public class Engine {
    private static Parser parser;
    private static MainFrame gui;
    private static GameManager gameManager;

    private Engine() {}

    public static void initialize(MainFrame gui) {
        try {
            Engine.gui = gui;

            AtomicBoolean crashed = new AtomicBoolean(false);
            Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread t, Throwable e) {
                    e.printStackTrace();
                    GUIManager.showFatalError("Thread " + t.getName() + ": " + e.getMessage());
                    crashed.set(true);
                }
            });

            DBManager.createDB();
            gameManager = GameManager.getInstance();
            gameManager.initialize();

            Set<String> stopwords = Utils.loadFileListInSet(new File("resources/stopwords"));
            parser = new Parser(stopwords, gameManager.getCommands());

            if (!crashed.get()) {
                GUIManager.createWeatherProgressMonitor();
                commandPerformed(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            GUIManager.showFatalError(e.getMessage());
        }
    }

    public static void commandPerformed(String command) {
        new Thread("Engine") {
            public void run() {
                if (!WeatherFetcher.isRaining()) {
                    SoundManager.playWav("resources/sound/ambience.wav", Mode.MUSIC);
                } else {
                    SoundManager.playWav("resources/sound/rainAmbience.wav", Mode.MUSIC);
                }

                if (gameManager.getCurrentRoom() instanceof PlayableRoom) {
                    PlayableRoom currentRoom = (PlayableRoom) gameManager.getCurrentRoom();
                    ParserResult p = parser.parse(command, currentRoom.getObjects(), gameManager.getInventory());

                    if (command == null) {
                        GUIManager.updateRoomInformations(currentRoom, gameManager.getPreviousRoom());
                        return;
                    }

                    if (p == null || p.getCommand() == null) {
                        GUIManager.appendOutput("Non capisco quello che mi vuoi dire.");
                        GUIManager.printOutput();
                    } else {
                        processCommand(p);
                    }
                } else if (gameManager.getCurrentRoom() instanceof CutsceneRoom) {
                    CutsceneRoom currentRoom = (CutsceneRoom) gameManager.getCurrentRoom();

                    GUIManager.updateRoomInformations(currentRoom, gameManager.getPreviousRoom());
                    GUIManager.waitUntilEnterIsPressed();

                    if (!currentRoom.isFinalRoom()) {
                        gameManager.proceedToNextRoom();
                    } else {
                        gui.dispatchEvent(new WindowEvent(gui, WindowEvent.WINDOW_CLOSING));
                    }
                }

                commandPerformed(null);
            }
        }.start();
    }

    private static void processCommand(ParserResult p) {
        PlayableRoom currentPlayableRoom = (PlayableRoom) gameManager.getCurrentRoom();
        boolean actionPerformed = false;

        // TODO controlla tutti i getList e il check != null altrimenti da nullPointer
        CommandType commandType = p.getCommand().getType();

        if (isMovementCommand(commandType)) {
            if (isMovementGranted(commandType)) {
                gameManager.moveTo(currentPlayableRoom.getRoomAt(commandType));
                actionPerformed = true;
            }
        } else {
            AbstractEntity roomObj = p.getRoomObject();
            AbstractEntity invObj = p.getInvObject();
            AbstractEntity anyObj = p.getObject();

            switch (commandType) {
                case SAVE: {
                    DBManager.save(gameManager);

                    GUIManager.appendOutput("Partita salvata correttamente!");
                    break;
                }
                case INVENTORY: {
                    if (!gameManager.getInventory().isEmpty()) {
                        StringBuilder stringBuilder = new StringBuilder("Nel tuo inventario ci sono:");

                        for (AbstractEntity obj : gameManager.getInventory().getObjects()) {
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

                            actionPerformed = pickupObj.pickup(gameManager.getInventory());
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

                            actionPerformed = container.insert(invObj, gameManager.getInventory());
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

                                    actionPerformed = container.insert(invObj, gameManager.getInventory());
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

        if (gameManager.getCurrentRoom() instanceof PlayableRoom) {
            currentPlayableRoom = (PlayableRoom) gameManager.getCurrentRoom();
            currentPlayableRoom.processRoomLighting(gameManager.getInventory().getObjects());
        } else {
            currentPlayableRoom = null;
        }

        if (actionPerformed) {
            GUIManager.increaseActionsCounter();
            gameManager.getInventory().checkForDestroyableItems();
        }

        if (gameManager.getCurrentMoveInfos().isMovementAttempt()) {
            if (!gameManager.getCurrentMoveInfos().isPositionChanged() && currentPlayableRoom != null
                    && currentPlayableRoom.isCurrentlyDark()) {
                GUIManager.appendOutput("Meglio non avventurarsi nel buio.");
            } else if (gameManager.getCurrentMoveInfos().isRoomBlockedByDoor()) {
                GUIManager.appendOutput("La porta é chiusa.");
            } else if (gameManager.getCurrentMoveInfos().isRoomBlockedByWall()) {
                GUIManager.appendOutput(gameManager.getCurrentMoveInfos().getWall().getTrespassingWhenLockedText());
            } else if (gameManager.getCurrentMoveInfos().isPositionChanged()) {
                if (currentPlayableRoom != null && !currentPlayableRoom.isCurrentlyDark()) {
                    GUIManager.appendOutput(PlayableRoom.processRoomEvent(gameManager.getCurrentRoom()));
                }
            } else {
                GUIManager.appendOutput("Da quella parte non si puó andare.");
            }
        }

        if (gameManager.getCurrentMoveInfos().isWarp()) {
            gameManager.moveTo(gameManager.getCurrentMoveInfos().getWarpDestination());

            GUIManager.appendOutput(PlayableRoom.processRoomEvent(gameManager.getCurrentRoom()));
        }

        gameManager.getCurrentMoveInfos().reset();
    }

    private static boolean isMovementGranted(CommandType direction) {
        PlayableRoom currentRoom = (PlayableRoom) gameManager.getCurrentRoom();
        AbstractRoom requestedRoom = currentRoom.getRoomAt(direction);
        MoveInformations status = gameManager.getCurrentMoveInfos();

        status.setMovementAttempt(true);

        InvisibleWall wall = currentRoom.getMagicWall(direction);
        if (wall != null) {
            status.setRoomBlockedByWall(true);
            status.setWall(wall);
            return false;
        }

        if (!currentRoom.isCurrentlyDark()) {
            if (requestedRoom != null) {
                for (Door door : Entities.listCheckedEntities(Door.class, currentRoom.getObjects())) {
                    if (requestedRoom.equals(door.getBlockedRoom())) {
                        if (door.isOpen()) {
                            return true;
                        } else {
                            status.setRoomBlockedByDoor(true);
                            return false;
                        }
                    }
                }

                return true;
            } else {
                return false;
            }
        } else {
            if (requestedRoom != null) {
                if (requestedRoom.equals(gameManager.getPreviousRoom())) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
    }

    private static boolean isMovementCommand(CommandType commandType) {
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
}
