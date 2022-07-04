package di.uniba.map.b.adventure.games;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

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
import di.uniba.map.b.adventure.type.AdvItemWearable;
import di.uniba.map.b.adventure.type.AdvMagicWall;
import di.uniba.map.b.adventure.type.AdvObject;
import di.uniba.map.b.adventure.type.AdvPerson;
import di.uniba.map.b.adventure.type.Command;
import di.uniba.map.b.adventure.type.CommandType;
import di.uniba.map.b.adventure.type.MutablePlayableRoom;
import di.uniba.map.b.adventure.type.NonPlayableRoom;
import di.uniba.map.b.adventure.type.PickupEvent;
import di.uniba.map.b.adventure.type.PlayableRoom;
import di.uniba.map.b.adventure.type.Room;

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
                .registerSubtype(AdvPerson.class);

        RuntimeTypeAdapterFactory<Room> typeAdapterRooms = RuntimeTypeAdapterFactory
                .of(Room.class)
                .registerSubtype(PlayableRoom.class)
                .registerSubtype(MutablePlayableRoom.class)
                .registerSubtype(NonPlayableRoom.class);

        RuntimeTypeAdapterFactory<AdvEvent> typeAdapterEvents = RuntimeTypeAdapterFactory
                .of(AdvEvent.class)
                .registerSubtype(PickupEvent.class);

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
                    PlayableRoom objRoom = (PlayableRoom) room;
                    if (objRoom.getObjects() != null) {
                        for (AdvObject obj : objRoom.getObjects()) {
                            linkObjectsParent(obj);
                        }
                    }
                }

                getRooms().add(room);
            }

            setCurrentRoom(rooms.get(0));
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

    @Override
    public void nextMove(ParserOutput p, GameJFrame gui) {
        Triple<Boolean, Boolean, Boolean> triple = new Triple<>(false, false, false);
        boolean actionPerformed = false;
        PlayableRoom currentRoom = (PlayableRoom) getCurrentRoom();

        // TODO up e down e wear e put

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
                AdvObject obj = null;
                if (p.getObject() != null) {
                    obj = p.getObject();
                } else if (p.getInvObject() != null) {
                    obj = p.getInvObject();
                }

                if (obj.getDescription() != null) {
                    StringBuilder outString = new StringBuilder(obj.getDescription());

                    if (obj instanceof AdvItemContainer) {
                        AdvItemContainer container = (AdvItemContainer) obj;
                        if (!container.isOpenable()) {
                            container.setOpen(true);
                            if (!container.getList().isEmpty()) {
                                outString.append("<br>Noti la presenza di: ");
                                for (AdvObject advObject : container.getList()) {
                                    outString.append(advObject.getName() + ",");
                                }
                                outString.deleteCharAt(outString.length() - 1);
                            }
                        }
                    }

                    gui.appendTextEdtOutput(outString.toString(), false);
                } else {
                    gui.appendTextEdtOutput("Nulla di particolare.", false);
                }
            } else {
                gui.appendTextEdtOutput("Non trovo l'oggetto da esaminare.", false);
            }
        } else if (p.getCommand().getType() == CommandType.PICK_UP) {
            if (p.getObject() != null || p.getInvObject() != null) {
                if (p.getObject() instanceof AdvItem || p.getInvObject() instanceof AdvItem) {
                    AdvItem obj = null;
                    if (p.getObject() != null) {
                        obj = (AdvItem) p.getObject();
                    } else if (p.getInvObject() != null) {
                        obj = (AdvItem) p.getInvObject();
                    }

                    if (obj.isPickupable()) {
                        if (currentRoom instanceof MutablePlayableRoom) {
                            MutablePlayableRoom currentMutableRoom = (MutablePlayableRoom) currentRoom;
                            for (AdvEvent evt : currentRoom.getEvents()) {
                                if (evt instanceof PickupEvent) {
                                    PickupEvent pEvt = (PickupEvent) evt;
                                    if (pEvt.getInvokerObjectId() == obj.getId()) {
                                        // Execute event
                                        if (pEvt.isUpdatingParentRoom()) {
                                            currentMutableRoom.updateToNewRoom();
                                        }

                                        if (pEvt.getText() != null && !pEvt.getText().isEmpty()) {
                                            gui.appendTextEdtOutput(pEvt.getText(), false);
                                        }
                                    }
                                }
                            }
                        }

                        getInventory().add(obj);

                        for (AdvObject advObject : currentRoom.getObjects()) {
                            if (obj.getParent().equals(advObject)) {
                                AdvItemContainer container = (AdvItemContainer) advObject;
                                container.getList().remove(obj);
                            }
                        }

                        gui.appendTextEdtOutput(String.format("Hai raccolto %s",
                                obj.getName()), false);
                        actionPerformed = true;
                    } else {
                        gui.appendTextEdtOutput("Non puoi raccogliere questo oggetto.", false);
                    }
                }
            } else {
                gui.appendTextEdtOutput("Non trovo l'oggetto da raccogliere.", false);
            }
        } else if (p.getCommand().getType() == CommandType.OPEN) {
            if (p.getObject() != null || p.getInvObject() != null) {
                AdvObject obj = null;
                if (p.getObject() != null) {
                    obj = p.getObject();
                } else if (p.getInvObject() != null) {
                    obj = p.getInvObject();
                }

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
                AdvObject obj = null;
                if (p.getObject() != null) {
                    obj = p.getObject();
                } else if (p.getInvObject() != null) {
                    obj = p.getInvObject();
                }

                if (obj instanceof AdvItem) {
                    AdvItem item = (AdvItem) obj;
                    pushObject(item, gui);
                }
            } else {
                gui.appendTextEdtOutput("Non trovo l'oggetto da premere.", false);
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
                gui.appendTextEdtOutput(getCurrentRoom().getDescription(), false);

                actionPerformed = true;
            }
        }
        if (actionPerformed) {
            int oldCounterVal = Integer.parseInt(gui.getLblActionsCounter().getText());
            gui.getLblActionsCounter().setText(Integer.toString(oldCounterVal + 1));
        }
    }

    private boolean pushObject(AdvItem object, GameJFrame gui) {
        if (object.isPushable() && !object.isPushed()) {
            gui.appendTextEdtOutput(String.format("Hai premuto: %s",
                    object.getName()), false);
            return true;
        } else {
            gui.appendTextEdtOutput("Non ci sono oggetti da premere qui.", false);
            return false;
        }

    }

    private boolean openDoor(GameJFrame gui, AdvDoor door, AdvObject key) {
        if (door.isOpenable() && !door.isOpen() && !door.isLocked()) {
            door.setOpen(true);
            gui.appendTextEdtOutput(String.format("Hai aperto: %s",
                    door.getName()), false);
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
                        gui.appendTextEdtOutput(String.format("Hai aperto: %s",
                                door.getName()), false);
                        return true;
                    } else {
                        gui.appendTextEdtOutput("Non funziona.", false);
                    }
                } else {
                    gui.appendTextEdtOutput("È chiusa a chiave.", false);
                }
            }
        }
        return false;
    }

    private boolean openContainer(GameJFrame gui, AdvItemContainer container) {
        if (container.isOpenable() && !container.isOpen()) {
            StringBuilder outString = new StringBuilder(String.format("Hai aperto: %s <br>",
                    container.getName()));
            if (!container.getList().isEmpty()) {
                outString.append(String.format("%s contiene: <br>",
                        container.getName()));

                for (AdvObject obj : container.getList()) {
                    outString.append(obj.getName() + "<br>");
                }
            }
            container.setOpen(true);
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
