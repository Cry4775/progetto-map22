package di.uniba.map.b.adventure.games;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import di.uniba.map.b.adventure.CommandsLoader;
import di.uniba.map.b.adventure.GameDescription;
import di.uniba.map.b.adventure.GameJFrame;
import di.uniba.map.b.adventure.RoomsLoader;
import di.uniba.map.b.adventure.entities.AbstractEntity;
import di.uniba.map.b.adventure.entities.AdvDoorBlocked;
import di.uniba.map.b.adventure.entities.AdvDoorOpenable;
import di.uniba.map.b.adventure.entities.AdvFire;
import di.uniba.map.b.adventure.entities.AdvMagicWall;
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
import di.uniba.map.b.adventure.parser.ParserOutput;
import di.uniba.map.b.adventure.type.AbstractRoom;
import di.uniba.map.b.adventure.type.CommandType;
import di.uniba.map.b.adventure.type.ObjEvent;
import di.uniba.map.b.adventure.type.PlayableRoom;
import di.uniba.map.b.adventure.type.RoomEvent;

// TODO il database puó essere usato come lista di tutti gli oggetti, poi dal json si imposta tutto
// tramite id
public class HauntedHouseGame extends GameDescription {

    StringBuilder outString;

    @Override
    public void init() throws FileNotFoundException, IOException {
        outString = new StringBuilder();

        CommandsLoader commandsLoader = new CommandsLoader(getCommands());
        Thread tCommands = new Thread(commandsLoader, "CommandsLoader");

        RoomsLoader roomsLoader = new RoomsLoader(getRooms());
        Thread tRooms = new Thread(roomsLoader, "RoomsLoader");

        tCommands.start();
        tRooms.start();

        try {
            tCommands.join();
            tRooms.join();
            if (!roomsLoader.isExceptionThrown()) {
                setCurrentRoom(getRooms().get(0));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean isMovementGranted(CommandType direction) {
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

                            outString.append(pickupObj.pickup(getInventory()));
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
                        } else if (anyObj instanceof AdvDoorBlocked) {
                            AdvDoorBlocked fakeDoor = (AdvDoorBlocked) anyObj;

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
                        } else if (anyObj instanceof AdvFire) {
                            AdvFire fire = (AdvFire) anyObj;

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
            gui.appendTxtPane(outString.toString(), false);
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
