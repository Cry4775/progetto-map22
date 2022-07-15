package di.uniba.map.b.adventure.games;

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

import java.awt.Image;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import di.uniba.map.b.adventure.GameDescription;
import di.uniba.map.b.adventure.GameJFrame;
import di.uniba.map.b.adventure.RuntimeTypeAdapterFactory;
import di.uniba.map.b.adventure.Triple;
import di.uniba.map.b.adventure.parser.ParserOutput;
import di.uniba.map.b.adventure.type.AdvDoor;
import di.uniba.map.b.adventure.type.AdvEvent;
import di.uniba.map.b.adventure.type.AdvFakeDoor;
import di.uniba.map.b.adventure.type.AdvItem;
import di.uniba.map.b.adventure.type.AdvItemContainer;
import di.uniba.map.b.adventure.type.AdvItemFillable;
import di.uniba.map.b.adventure.type.AdvItemWearable;
import di.uniba.map.b.adventure.type.AdvMagicWall;
import di.uniba.map.b.adventure.type.AdvObject;
import di.uniba.map.b.adventure.type.AdvPerson;
import di.uniba.map.b.adventure.type.Command;
import di.uniba.map.b.adventure.type.CommandType;
import di.uniba.map.b.adventure.type.EventType;
import di.uniba.map.b.adventure.type.MutablePlayableRoom;
import di.uniba.map.b.adventure.type.NonPlayableRoom;
import di.uniba.map.b.adventure.type.ObjEvent;
import di.uniba.map.b.adventure.type.PlayableRoom;
import di.uniba.map.b.adventure.type.Room;
import di.uniba.map.b.adventure.type.RoomEvent;

// Se tutto viene caricato da file allora si può cancellare
public class HauntedHouseGame extends GameDescription {

    @Override
    public void init() throws FileNotFoundException, IOException {
        loadCommands();
        loadRooms();
    }

    private void loadCommands() throws FileNotFoundException, IOException {
        Gson gson = new Gson();

        Type commandsType = new TypeToken<List<Command>>() {
        }.getType();

        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(new FileInputStream("./resources/commands.json"), StandardCharsets.UTF_8))) {
            List<Command> commands = gson.fromJson(in, commandsType);

            for (Command command : commands) {
                getCommands().add(command);
            }
        }
    }

    private void loadRooms() throws FileNotFoundException, IOException {
        RuntimeTypeAdapterFactory<AdvObject> typeAdapterObjects = RuntimeTypeAdapterFactory
                .of(AdvObject.class)
                .registerSubtype(AdvItem.class)
                .registerSubtype(AdvItemContainer.class)
                .registerSubtype(AdvFakeDoor.class)
                .registerSubtype(AdvDoor.class)
                .registerSubtype(AdvMagicWall.class)
                .registerSubtype(AdvItemWearable.class)
                .registerSubtype(AdvItemFillable.class)
                .registerSubtype(AdvPerson.class);

        RuntimeTypeAdapterFactory<Room> typeAdapterRooms = RuntimeTypeAdapterFactory
                .of(Room.class)
                .registerSubtype(PlayableRoom.class)
                .registerSubtype(MutablePlayableRoom.class)
                .registerSubtype(NonPlayableRoom.class);

        RuntimeTypeAdapterFactory<AdvEvent> typeAdapterEvents = RuntimeTypeAdapterFactory
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
                .registerTypeAdapterFactory(typeAdapterEvents)
                .create();
        Type roomsType = new TypeToken<List<Room>>() {
        }.getType();

        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(new FileInputStream("./resources/rooms.json"), StandardCharsets.UTF_8))) {
            List<Room> rooms = gson.fromJson(in, roomsType);

            linkRooms(rooms);

            for (Room room : rooms) {

                if (room instanceof PlayableRoom) {
                    PlayableRoom playableRoom = (PlayableRoom) room;
                    if (playableRoom.getObjects() != null) {
                        for (AdvObject obj : playableRoom.getObjects()) {
                            linkObjectsParent(obj);
                        }
                    }
                }

                getRooms().add(room);
            }

            List<AdvObject> objects = listAllObjects();

            for (AdvObject advObject : objects) {
                linkObjectsEventsReference(advObject, getRooms());
                if (advObject instanceof AdvItemFillable) {
                    linkItemFillablesReference((AdvItemFillable) advObject, objects);
                }
            }

            setCurrentRoom(rooms.get(0));
        }
    }

    private List<AdvObject> listAllObjects() {
        List<AdvObject> objects = new ArrayList<>();

        List<PlayableRoom> rooms = new ArrayList<>(getRooms().stream()
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

    private void unpackContainerObj(List<AdvObject> objects) {
        List<AdvObject> tempObjects = new ArrayList<>();
        for (AdvObject obj : objects) {
            if (obj instanceof AdvItemContainer) {
                AdvItemContainer container = (AdvItemContainer) obj;
                if (container.getList() != null) {
                    List<AdvObject> copyContainer = new ArrayList<>(container.getList());
                    unpackContainerObj(copyContainer);
                    tempObjects.addAll(copyContainer);
                }
            }
        }
        objects.addAll(tempObjects);
    }

    private void linkItemFillablesReference(AdvItemFillable obj, List<AdvObject> objects) {
        if (obj.getFilledWithItemId() != null) {
            objects.stream()
                    .filter(AdvItem.class::isInstance)
                    .map(AdvItem.class::cast)
                    .filter(reqItem -> reqItem.getId() == obj.getFilledWithItemId())
                    .forEach(reqItem -> obj.setFilledWithItem(reqItem));
        }
    }

    private void linkObjectsParent(AdvObject obj) {
        if (obj instanceof AdvItemContainer) {
            AdvItemContainer container = (AdvItemContainer) obj;
            if (container.getList() != null) {
                for (AdvObject itemUncasted : container.getList()) {
                    if (itemUncasted instanceof AdvItem) {
                        AdvItem item = (AdvItem) itemUncasted;
                        item.setParent(container);
                        linkObjectsParent(item);
                    }
                }
            }
        }
    }

    private void linkObjectsEventsReference(AdvObject obj, List<Room> rooms) {
        if (obj.getEvents() != null) {
            for (ObjEvent objEvent : obj.getEvents()) {
                if (objEvent.getUpdateTargetRoomId() != null) {
                    rooms.stream()
                            .filter(MutablePlayableRoom.class::isInstance)
                            .map(MutablePlayableRoom.class::cast)
                            .filter(room -> objEvent.getUpdateTargetRoomId() == room.getId())
                            .forEach(room -> objEvent.setUpdateTargetRoom(room));
                }
            }
        }
    }

    @Override
    public void nextMove(ParserOutput p, GameJFrame gui) {
        Triple<Boolean, Boolean, Boolean> triple = new Triple<>(false, false, false);
        boolean actionPerformed = false;
        PlayableRoom currentRoom = (PlayableRoom) getCurrentRoom();

        // TODO wear e put
        // TODO controlla tutti i getList e il check != null altrimenti da nullPointer

        if (p.getCommand().getType() == CommandType.NORTH) {
            triple = moveToCardinalDirection(currentRoom.getNorth());
        } else if (p.getCommand().getType() == CommandType.NORTH_EAST) {
            triple = moveToCardinalDirection(currentRoom.getNorthEast());
        } else if (p.getCommand().getType() == CommandType.NORTH_WEST) {
            triple = moveToCardinalDirection(currentRoom.getNorthWest());
        } else if (p.getCommand().getType() == CommandType.SOUTH) {
            triple = moveToCardinalDirection(currentRoom.getSouth());
        } else if (p.getCommand().getType() == CommandType.SOUTH_EAST) {
            triple = moveToCardinalDirection(currentRoom.getSouthEast());
        } else if (p.getCommand().getType() == CommandType.SOUTH_WEST) {
            triple = moveToCardinalDirection(currentRoom.getSouthWest());
        } else if (p.getCommand().getType() == CommandType.EAST) {
            triple = moveToCardinalDirection(currentRoom.getEast());
        } else if (p.getCommand().getType() == CommandType.WEST) {
            triple = moveToCardinalDirection(currentRoom.getWest());
        } else if (p.getCommand().getType() == CommandType.UP) {
            triple = moveToCardinalDirection(currentRoom.getUp());
        } else if (p.getCommand().getType() == CommandType.DOWN) {
            triple = moveToCardinalDirection(currentRoom.getDown());
        } else if (p.getCommand().getType() == CommandType.INVENTORY) {
            if (!getInventory().isEmpty()) {
                StringBuilder outString = new StringBuilder("Nel tuo inventario ci sono:");
                for (AdvObject obj : getInventory()) {
                    outString.append("<br> - " + obj.getName());
                }
                gui.appendTextEdtOutput(outString.toString(), false);
            } else {
                gui.appendTextEdtOutput("Il tuo inventario è vuoto.", false);
            }
        } else if (p.getCommand().getType() == CommandType.LOOK_AT) {
            if (p.getObject() != null || p.getInvObject() != null) {
                AdvObject obj = getObjectFromParser(p);

                actionPerformed = lookAt(gui, obj);
            } else {
                gui.appendTextEdtOutput("Non trovo l'oggetto da esaminare.", false);
            }
        } else if (p.getCommand().getType() == CommandType.PICK_UP) {
            if (p.getObject() != null || p.getInvObject() != null) {
                if (p.getObject() instanceof AdvItem || p.getInvObject() instanceof AdvItem) {
                    AdvItem item = (AdvItem) getObjectFromParser(p);

                    if (item.isPickupable() && !item.isPicked()) {
                        actionPerformed = pickUpItem(gui, item, currentRoom.getObjects());
                    } else if (item.getParent() != null && item.getParent() instanceof AdvItemFillable) {
                        gui.appendTextEdtOutput("Non puoi riprendertelo.", false);
                    } else if (item.isPicked()) {
                        gui.appendTextEdtOutput("L'oggetto è già nel tuo inventario.", false);
                    } else {
                        gui.appendTextEdtOutput("Non puoi raccogliere questo oggetto.", false);
                    }
                }
            } else {
                gui.appendTextEdtOutput("Non trovo l'oggetto da raccogliere.", false);
            }
        } else if (p.getCommand().getType() == CommandType.OPEN) {
            if (p.getObject() != null || p.getInvObject() != null) {
                AdvObject obj = getObjectFromParser(p);

                if (obj instanceof AdvItemContainer) {
                    AdvItemContainer container = (AdvItemContainer) obj;
                    actionPerformed = openContainer(gui, container);
                } else if (obj instanceof AdvDoor) {
                    AdvDoor door = (AdvDoor) obj;
                    AdvObject key = p.getInvObject();
                    actionPerformed = openDoor(gui, door, key);
                } else if (obj instanceof AdvFakeDoor) {
                    AdvFakeDoor fakeDoor = (AdvFakeDoor) obj;
                    gui.appendTextEdtOutput(fakeDoor.getOpenEventText(), false);
                } else {
                    gui.appendTextEdtOutput("Non puoi aprire questo oggetto.", false);
                }
            } else {
                gui.appendTextEdtOutput("Non trovo l'oggetto da aprire.", false);
            }
        } else if (p.getCommand().getType() == CommandType.PUSH) {
            if (p.getObject() != null || p.getInvObject() != null) {
                AdvObject obj = getObjectFromParser(p);

                if (obj instanceof AdvItem) {
                    AdvItem item = (AdvItem) obj;
                    pushObject(item, gui);
                }
            } else {
                gui.appendTextEdtOutput("Non trovo l'oggetto da premere.", false);
            }
        } else if (p.getCommand().getType() == CommandType.PULL) {
            if (p.getObject() != null || p.getInvObject() != null) {
                if (p.getObject() instanceof AdvItem || p.getInvObject() instanceof AdvItem) {
                    AdvItem item = (AdvItem) getObjectFromParser(p);

                    actionPerformed = pullObject(item, gui);
                }
            } else {
                gui.appendTextEdtOutput("Non trovo l'oggetto da tirare.", false);
            }
        } else if (p.getCommand().getType() == CommandType.MOVE) {
            if (p.getObject() != null) {
                if (p.getObject() instanceof AdvItem) {
                    AdvItem item = (AdvItem) p.getObject();

                    actionPerformed = moveItem(item, gui);
                }
            } else {
                gui.appendTextEdtOutput("Non trovo l'oggetto da spostare.", false);
            }
        } else if (p.getCommand().getType() == CommandType.INSERT) {
            if (p.getObject() != null && p.getInvObject() != null) {
                AdvObject obj = getObjectFromParser(p);
                AdvObject invObj = p.getInvObject();

                if (obj instanceof AdvItemFillable) {
                    AdvItemFillable fillableObj = (AdvItemFillable) obj;
                    if (!fillableObj.isFilled()) {
                        if (fillableObj.getFilledWithItem().equals(invObj)) {
                            fillableObj.setFilledWithItem((AdvItem) invObj);
                            fillableObj.setFilled(true);
                            ((AdvItem) invObj).setParent(fillableObj);
                            getInventory().remove(invObj);

                            StringBuilder outString = new StringBuilder("Hai inserito: " + invObj.getName());
                            outString.append(handleObjEvent(obj.getEvent(EventType.INSERT)));

                            gui.appendTextEdtOutput(outString.toString(), false);
                        } else {
                            gui.appendTextEdtOutput("Non puoi inserirci questo oggetto.", false);
                        }
                    } else {
                        gui.appendTextEdtOutput("Non puoi inserirci altri oggetti.", false);
                    }
                }
            } else if (p.getObject() == null) {
                gui.appendTextEdtOutput("Non trovo l'oggetto in cui inserire.", false);
            } else {
                gui.appendTextEdtOutput("Non trovo l'oggetto da inserire.", false);
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
            int oldCounterVal = Integer.parseInt(gui.getLblActionsCounter().getText());
            gui.getLblActionsCounter().setText(Integer.toString(oldCounterVal + 1));
        }
    }

    private boolean moveItem(AdvItem item, GameJFrame gui) {
        if (item.isMovable() && !item.isMoved()) {
            item.setMoved(true);
            StringBuilder outString = new StringBuilder("Hai spostato: " + item.getName());

            outString.append(handleObjEvent(item.getEvent(EventType.MOVE)));

            gui.appendTextEdtOutput(outString.toString(), false);

            return true;
        } else if (item.isMoved()) {
            gui.appendTextEdtOutput("È stato già spostato.", false);
            return false;
        } else {
            gui.appendTextEdtOutput("Non puoi spostarlo.", false);
            return false;
        }
    }

    private AdvObject getObjectFromParser(ParserOutput p) {
        if (p.getObject() != null) {
            return p.getObject();
        } else if (p.getInvObject() != null) {
            return p.getInvObject();
        }
        return null;
    }

    private boolean lookAt(GameJFrame gui, AdvObject obj) {
        StringBuilder outString = new StringBuilder();

        if (obj.getDescription() != null) {
            outString.append(obj.getDescription());
        }

        if (obj instanceof AdvItemContainer) {
            AdvItemContainer container = (AdvItemContainer) obj;
            if (!container.isOpenable()) {
                container.setOpen(true);
                if (container.getList() != null && !container.getList().isEmpty()) {
                    outString.append("<br>Noti la presenza di:");
                    for (AdvObject advObject : container.getList()) {
                        outString.append(" " + advObject.getName() + ",");
                    }
                    outString.deleteCharAt(outString.length() - 1);
                }
            }
        } else if (obj instanceof AdvDoor) {
            AdvDoor door = (AdvDoor) obj;
            if (!door.isOpen()) {
                outString.append("È chiusa.");
            } else if (door.isLocked()) {
                outString.append("È chiusa a chiave.");
            } else if (door.isOpen()) {
                outString.append("È aperta.");
            }
        } else if (obj instanceof AdvItemFillable) {
            AdvItemFillable fillable = (AdvItemFillable) obj;
            if (fillable.isFilled()) {
                outString.append("<br>Contiene: " + fillable.getFilledWithItem().getName());
            } else {
                outString.append("<br>È vuoto.");
            }
        }

        if (outString.toString().isEmpty()) {
            gui.appendTextEdtOutput("Nulla di particolare.", false);
        } else {
            outString.append(handleObjEvent(obj.getEvent(EventType.LOOK_AT)));

            gui.appendTextEdtOutput(outString.toString(), false);
        }

        return true;
    }

    private boolean pickUpItem(GameJFrame gui, AdvItem item, List<AdvObject> roomObjects) {
        if (item.isPickupableWithFillableItem()) {
            if (!item.isPicked()) {
                boolean canProceed = false;
                for (AdvObject invObject : getInventory()) {
                    if (invObject instanceof AdvItemFillable) {
                        AdvItemFillable invFillable = (AdvItemFillable) invObject;
                        if (invFillable.getFilledWithItem().equals(item)) {
                            invFillable.setFilled(true);
                            canProceed = true;
                            break;
                        }
                    }
                }
                if (!canProceed) {
                    gui.appendTextEdtOutput("Non puoi prenderlo senza lo strumento adatto.", false);
                    return false;
                }
            } else {
                gui.appendTextEdtOutput("L'hai già preso.", false);
                return false;
            }
        } else if (item.getParent() != null && item.getParent() instanceof AdvItemFillable) {
            gui.appendTextEdtOutput("Non puoi riprendertelo.", false);
        } else {
            getInventory().add(item);
        }

        item.setPicked(true);

        if (item.getParent() != null) {
            // Check if it's an obj inside something and remove it from its list

            AdvItemContainer parentContainer = (AdvItemContainer) item.getParent();
            parentContainer.getList().remove(item);
            item.setParent(null);
        } else {
            roomObjects.remove(item);
        }

        StringBuilder outString = new StringBuilder("Hai raccolto: " + item.getName());
        outString.append(handleObjEvent(item.getEvent(EventType.PICK_UP)));

        gui.appendTextEdtOutput(outString.toString(), false);
        return true;
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

    private String handleObjEvent(ObjEvent evt) {
        if (evt != null) {
            if (evt.isUpdatingParentRoom()) {
                if (getCurrentRoom() instanceof MutablePlayableRoom) {
                    MutablePlayableRoom currentMutableRoom = (MutablePlayableRoom) getCurrentRoom();
                    currentMutableRoom.updateToNewRoom();
                }
            }

            if (evt.isUpdatingAnotherRoom()) {
                if (evt.getUpdateTargetRoom() != null) {
                    evt.getUpdateTargetRoom().updateToNewRoom();
                }
            }

            StringBuilder outString = new StringBuilder();

            if (evt.getText() != null && !evt.getText().isEmpty()) {
                outString.append("<br><br>" + evt.getText());
            }

            evt.setTriggered(true);

            return outString.toString();
        }
        return "";
    }

    private boolean pullObject(AdvItem object, GameJFrame gui) {
        if (object.isPullable() && !object.isPulled()) {
            object.setPulled(true);
            StringBuilder outString = new StringBuilder("Hai tirato: " + object.getName());

            outString.append(handleObjEvent(object.getEvent(EventType.PULL)));

            gui.appendTextEdtOutput(outString.toString(), false);
            return true;
        } else if (object.isPulled()) {
            gui.appendTextEdtOutput("È stato già tirato.", false);
            return false;
        } else {
            gui.appendTextEdtOutput("Non puoi tirarlo.", false);
            return false;
        }
    }

    private boolean pushObject(AdvItem object, GameJFrame gui) {
        if (object.isPushable() && !object.isPushed()) {
            object.setPushed(true);

            StringBuilder outString = new StringBuilder();
            outString.append("Hai premuto: " + object.getName());
            outString.append(handleObjEvent(object.getEvent(EventType.PUSH)));
            gui.appendTextEdtOutput(outString.toString(), false);
            return true;
        } else if (object.isPushed()) {
            gui.appendTextEdtOutput("È stato già premuto.", false);
            return false;
        } else {
            gui.appendTextEdtOutput("Non puoi premerlo.", false);
            return false;
        }

    }

    private boolean openDoor(GameJFrame gui, AdvDoor door, AdvObject key) {
        StringBuilder outString = new StringBuilder();
        if (door.isOpenable() && !door.isOpen() && !door.isLocked()) {
            door.setOpen(true);

            outString.append("Hai aperto: " + door.getName());
            outString.append(handleObjEvent(door.getEvent(EventType.OPEN_UNLOCKED)));

            gui.appendTextEdtOutput(outString.toString(), false);

            return true;
        } else if (door.isLocked()) {
            if (door.getBlockedRoomId() != 0) {
                if (key != null) {
                    if (key.getId() == door.getUnlockedWithItemId()) {
                        door.setLocked(false);
                        door.setOpen(true);

                        getRooms().stream()
                                .filter(room -> room.getId() == door.getBlockedRoomId())
                                .forEach(room -> room.setVisible(true));

                        getInventory().remove(key);

                        outString.append("Hai aperto: " + door.getName());
                        outString.append(handleObjEvent(door.getEvent(EventType.OPEN_UNLOCKED)));

                        gui.appendTextEdtOutput(outString.toString(), false);
                        return true;
                    } else {
                        outString.append("Non funziona.");
                        outString.append(handleObjEvent(door.getEvent(EventType.OPEN_LOCKED)));
                        gui.appendTextEdtOutput(outString.toString(), false);
                    }
                } else {
                    outString.append("È chiusa a chiave.");
                    outString.append(handleObjEvent(door.getEvent(EventType.OPEN_LOCKED)));
                    gui.appendTextEdtOutput(outString.toString(), false);
                }
            }
        }
        return false;
    }

    private boolean openContainer(GameJFrame gui, AdvItemContainer container) {
        if (container.isOpenable() && !container.isOpen()) {
            container.setOpen(true);
            StringBuilder outString = new StringBuilder("Hai aperto: " + container.getName());

            if (container.getList() != null && !container.getList().isEmpty()) {
                outString.append(" che contiene:");
                for (AdvObject advObject : container.getList()) {
                    outString.append(" " + advObject.getName() + ",");
                }
                outString.deleteCharAt(outString.length() - 1);
            }

            outString.append(handleObjEvent(container.getEvent(EventType.OPEN_CONTAINER)));

            gui.appendTextEdtOutput(outString.toString(), false);
            return true;
        } else if (container.isOpenable() && container.isOpen()) {
            gui.appendTextEdtOutput("È già aperto.", false);
        } else {
            gui.appendTextEdtOutput("Non puoi aprire questo oggetto.", false);
        }
        return false;
    }

    private void linkRooms(List<Room> rooms) {
        setRoomsDirection(rooms, PlayableRoom::getEastId, PlayableRoom::setEast, PlayableRoom.class);
        setRoomsDirection(rooms, PlayableRoom::getWestId, PlayableRoom::setWest, PlayableRoom.class);
        setRoomsDirection(rooms, PlayableRoom::getNorthId, PlayableRoom::setNorth, PlayableRoom.class);
        setRoomsDirection(rooms, PlayableRoom::getSouthId, PlayableRoom::setSouth, PlayableRoom.class);
        setRoomsDirection(rooms, PlayableRoom::getNorthEastId, PlayableRoom::setNorthEast, PlayableRoom.class);
        setRoomsDirection(rooms, PlayableRoom::getNorthWestId, PlayableRoom::setNorthWest, PlayableRoom.class);
        setRoomsDirection(rooms, PlayableRoom::getSouthEastId, PlayableRoom::setSouthEast, PlayableRoom.class);
        setRoomsDirection(rooms, PlayableRoom::getSouthWestId, PlayableRoom::setSouthWest, PlayableRoom.class);
        setRoomsDirection(rooms, PlayableRoom::getUpId, PlayableRoom::setUp, PlayableRoom.class);
        setRoomsDirection(rooms, PlayableRoom::getDownId, PlayableRoom::setDown, PlayableRoom.class);
        setRoomsDirection(rooms, NonPlayableRoom::getNextRoomId, NonPlayableRoom::setNextRoom, NonPlayableRoom.class);
    }

    private <T extends Room> void setRoomsDirection(List<Room> rooms,
            Function<T, Integer> directionIdGetter,
            BiConsumer<T, Room> directionSetter, Class<T> clazz) {
        rooms.stream()
                .filter(clazz::isInstance)
                .map(clazz::cast)
                .filter(room -> directionIdGetter.apply(room) != null)
                .forEach(room -> rooms.stream()
                        .filter(linkedRoom -> linkedRoom.getId() == directionIdGetter.apply(room))
                        .forEach(linkedRoom -> directionSetter.accept(room, linkedRoom)));
    }

    private Triple<Boolean, Boolean, Boolean> moveToCardinalDirection(Room room) {
        if (room != null) {
            PlayableRoom currentRoom = (PlayableRoom) getCurrentRoom();
            if (currentRoom.getObjects() != null) {
                for (AdvObject obj : currentRoom.getObjects()) {
                    if (obj instanceof AdvDoor) {
                        AdvDoor door = (AdvDoor) obj;
                        if (door.getBlockedRoomId() == room.getId()
                                && door.isOpen()) {
                            setCurrentRoom(room);
                            return new Triple<>(true, false, false);
                        } else if (door.getBlockedRoomId() == room.getId()
                                && !door.isOpen()) {
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
