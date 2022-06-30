package di.uniba.map.b.adventure.games;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
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
import di.uniba.map.b.adventure.Pair;
import di.uniba.map.b.adventure.RuntimeTypeAdapterFactory;
import di.uniba.map.b.adventure.parser.ParserOutput;
import di.uniba.map.b.adventure.type.AdvObject;
import di.uniba.map.b.adventure.type.AdvObjectContainer;
import di.uniba.map.b.adventure.type.Command;
import di.uniba.map.b.adventure.type.CommandType;
import di.uniba.map.b.adventure.type.ObjectEvent;
import di.uniba.map.b.adventure.type.Room;

// Se tutto viene caricato da file allora si può cancellare
public class HauntedHouseGame extends GameDescription {

    List<ObjectEvent> objectEvents;

    @Override
    public void init() throws FileNotFoundException, IOException {
        loadCommands();
        loadRooms();
        objectEvents = loadEvents();
    }

    private void loadCommands() throws FileNotFoundException, IOException {
        Gson gson = new Gson();

        Type commandsType = new TypeToken<List<Command>>() {
        }.getType();

        try (BufferedReader in = new BufferedReader(new FileReader("./resources/commands.json"))) {
            List<Command> commands = gson.fromJson(in, commandsType);

            for (Command command : commands) {
                getCommands().add(command);
            }
        }
    }

    private void loadRooms() throws FileNotFoundException, IOException {
        RuntimeTypeAdapterFactory<AdvObject> runtimeTypeAdapterFactory = RuntimeTypeAdapterFactory
                .of(AdvObject.class)
                .registerSubtype(AdvObjectContainer.class)
                .registerSubtype(AdvObject.class);

        Gson gson = new GsonBuilder()
                .setExclusionStrategies(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        return Room.class.equals(f.getDeclaredClass());
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                })
                .registerTypeAdapterFactory(runtimeTypeAdapterFactory)
                .create();
        Type roomsType = new TypeToken<List<Room>>() {
        }.getType();

        try (BufferedReader in = new BufferedReader(new FileReader("./resources/rooms.json"))) {
            List<Room> rooms = gson.fromJson(in, roomsType);

            linkRooms(rooms);

            for (Room room : rooms) {
                getRooms().add(room);
            }

            setCurrentRoom(rooms.get(0));
        }
    }

    private List<ObjectEvent> loadEvents() throws FileNotFoundException, IOException {
        Gson gson = new GsonBuilder().create();
        Type objectEventsType = new TypeToken<List<ObjectEvent>>() {
        }.getType();

        try (BufferedReader in = new BufferedReader(new FileReader("./resources/events.json"))) {
            return gson.fromJson(in, objectEventsType);
        }
    }

    @Override
    public void nextMove(ParserOutput p, GameJFrame gui) {
        Pair<Boolean, Boolean> pair = new Pair<>(false, false);
        boolean actionPerformed = false;

        if (p.getCommand().getType() == CommandType.NORTH) {
            pair = moveToCardinalDirection(Room::getNorth);
        } else if (p.getCommand().getType() == CommandType.NORTH_EAST) {
            pair = moveToCardinalDirection(Room::getNorthEast);
        } else if (p.getCommand().getType() == CommandType.NORTH_WEST) {
            pair = moveToCardinalDirection(Room::getNorthWest);
        } else if (p.getCommand().getType() == CommandType.SOUTH) {
            pair = moveToCardinalDirection(Room::getSouth);
        } else if (p.getCommand().getType() == CommandType.SOUTH_EAST) {
            pair = moveToCardinalDirection(Room::getSouthEast);
        } else if (p.getCommand().getType() == CommandType.SOUTH_WEST) {
            pair = moveToCardinalDirection(Room::getSouthWest);
        } else if (p.getCommand().getType() == CommandType.EAST) {
            pair = moveToCardinalDirection(Room::getEast);
        } else if (p.getCommand().getType() == CommandType.WEST) {
            pair = moveToCardinalDirection(Room::getWest);
        } else if (p.getCommand().getType() == CommandType.INVENTORY) {
            gui.appendTextEdtOutput("Nel tuo inventario ci sono:");
            for (AdvObject obj : getInventory()) {
                gui.appendTextEdtOutput(obj.getName());
            }
        } else if (p.getCommand().getType() == CommandType.LOOK_AT) {
            if (getCurrentRoom().getLook() != null) {
                gui.appendTextEdtOutput(getCurrentRoom().getLook());
                actionPerformed = true;
            } else {
                gui.appendTextEdtOutput("Non c'è niente di interessante qui.");
                actionPerformed = true;
            }
        } else if (p.getCommand().getType() == CommandType.PICK_UP) {
            if (p.getObject() != null) {
                if (p.getObject().isPickupable()) {
                    getInventory().add(p.getObject());
                    getCurrentRoom().getObjects().remove(p.getObject());
                    gui.appendTextEdtOutput(String.format("Hai raccolto %s",
                            p.getObject().getName()));
                    handleEvents(p, gui);
                    actionPerformed = true;
                } else {
                    gui.appendTextEdtOutput("Non puoi raccogliere questo oggetto.");
                }
            } else {
                gui.appendTextEdtOutput("Non c'è niente da raccogliere qui.");
            }
        } else if (p.getCommand().getType() == CommandType.OPEN) {
            /*
             * ATTENZIONE: quando un oggetto contenitore viene aperto, tutti gli oggetti
             * contenuti
             * vengongo inseriti nella stanza o nell'inventario a seconda di dove si trova
             * l'oggetto contenitore.
             * Potrebbe non esssere la soluzione ottimale.
             */
            if (p.getObject() == null && p.getInvObject() == null) {
                gui.appendTextEdtOutput("Non c'è niente da aprire qui.");
            } else {
                if (p.getObject() != null) {
                    if (p.getObject().isOpenable() && !p.getObject().isOpen()) {
                        if (p.getObject() instanceof AdvObjectContainer) {
                            gui.appendTextEdtOutput(String.format("Hai aperto: %s",
                                    p.getObject().getName()));
                            AdvObjectContainer container = (AdvObjectContainer) p.getObject();
                            if (!container.getList().isEmpty()) {
                                gui.appendTextEdtOutput(String.format("%s contiene: ",
                                        container.getName()));
                                StringBuilder string = new StringBuilder();
                                for (AdvObject obj : container.getList()) {
                                    getCurrentRoom().getObjects().add(obj);
                                    string.append(obj.getName() + "<br>");
                                    container.getList().remove(obj);
                                }
                                gui.appendTextEdtOutput(string.toString());
                            }
                            actionPerformed = true;
                        } else {
                            // Ad esempio porta
                            gui.appendTextEdtOutput(String.format("Hai aperto: %s",
                                    p.getObject().getName()));
                            p.getObject().setOpen(true);
                            actionPerformed = true;
                        }
                    } else {
                        gui.appendTextEdtOutput("Non puoi aprire questo oggetto.");
                    }
                }
                if (p.getInvObject() != null) {
                    if (p.getInvObject().isOpenable() && !p.getInvObject().isOpen()) {
                        if (p.getInvObject() instanceof AdvObjectContainer) {
                            gui.appendTextEdtOutput(String.format("Hai aperto: %s",
                                    p.getInvObject().getName()));
                            AdvObjectContainer container = (AdvObjectContainer) p.getInvObject();
                            if (!container.getList().isEmpty()) {
                                gui.appendTextEdtOutput(String.format("%s contiene: ",
                                        container.getName()));
                                StringBuilder string = new StringBuilder();
                                for (AdvObject obj : container.getList()) {
                                    getInventory().add(obj);
                                    string.append(obj.getName() + "<br>");
                                    container.getList().remove(obj);
                                }
                            }
                            actionPerformed = true;
                        } else {
                            gui.appendTextEdtOutput(String.format("Hai aperto nel tuo inventario: %s",
                                    p.getInvObject().getName()));
                            p.getInvObject().setOpen(true);
                            actionPerformed = true;
                        }
                    } else {
                        gui.appendTextEdtOutput("Non puoi aprire questo oggetto.");
                    }
                }
            }
        } else if (p.getCommand().getType() == CommandType.PUSH) {
            if (p.getObject() != null && p.getObject().isPushable()) {
                gui.appendTextEdtOutput(String.format("Hai premuto: %s",
                        p.getObject().getName()));
                actionPerformed = true;
            } else if (p.getInvObject() != null && p.getInvObject().isPushable()) {
                gui.appendTextEdtOutput(String.format("Hai premuto: %s",
                        p.getInvObject().getName()));
                actionPerformed = true;
            } else {
                gui.appendTextEdtOutput("Non ci sono oggetti da premere qui.");
            }
        }

        if (pair.getFirst() || pair.getSecond()) {
            boolean move = pair.getFirst();
            boolean noRoom = pair.getSecond();
            if (noRoom) {
                gui.appendTextEdtOutput("Da quella parte non si può andare.");
            } else if (move) {
                gui.getLblRoomName().setText(getCurrentRoom().getName());

                Image roomImg = new ImageIcon(getCurrentRoom().getImgPath()).getImage()
                        .getScaledInstance(581, 300, Image.SCALE_SMOOTH);
                gui.getLblRoomImage().setIcon(new ImageIcon(roomImg));
                setCompassLabels(gui);
                gui.appendTextEdtOutput(getCurrentRoom().getDescription());

                actionPerformed = true;
            }
        }

        if (actionPerformed) {
            int oldCounterVal = Integer.parseInt(gui.getLblActionsCounter().getText());
            gui.getLblActionsCounter().setText(Integer.toString(oldCounterVal + 1));
        }
    }

    private void handleEvents(ParserOutput p, GameJFrame gui) {
        for (ObjectEvent event : objectEvents) {
            if (event.getType().equals(p.getCommand().getType())) {
                if (event.getRoomId() != 0 && event.getObjectId() != 0) {
                    if (getCurrentRoom().getId() == event.getRoomId()
                            && p.getObject().getId() == event.getObjectId()) {
                        if (event.getText() != null && !event.getText().isEmpty()) {
                            gui.appendTextEdtOutput(event.getText());
                        }
                        if (event.isEndsTheGame()) {
                            // TODO chiudi
                        }
                        // TODO eventi oggetti
                        if (event.isOneTime()) {
                            objectEvents.remove(event);
                            return;
                        }
                    }
                } else if (event.getRoomId() != 0) {
                    if (getCurrentRoom().getId() == event.getRoomId()) {
                        // TODO eventi stanze
                        if (event.getText() != null && !event.getText().isEmpty()) {
                            gui.appendTextEdtOutput(event.getText());
                        }
                        if (event.isOneTime()) {
                            objectEvents.remove(event);
                            return;
                        }
                    }
                }
            }
        }
    }

    private void linkRooms(List<Room> rooms) {
        setCardinalDirections(rooms, Room::getEastId, Room::setEast);
        setCardinalDirections(rooms, Room::getWestId, Room::setWest);
        setCardinalDirections(rooms, Room::getNorthId, Room::setNorth);
        setCardinalDirections(rooms, Room::getSouthId, Room::setSouth);
        setCardinalDirections(rooms, Room::getNorthEastId, Room::setNorthEast);
        setCardinalDirections(rooms, Room::getNorthWestId, Room::setNorthWest);
        setCardinalDirections(rooms, Room::getSouthEastId, Room::setSouthEast);
        setCardinalDirections(rooms, Room::getSouthWestId, Room::setSouthWest);
    }

    private void setCardinalDirections(List<Room> rooms,
            Function<Room, Integer> directionGetter,
            BiConsumer<Room, Room> directionSetter) {

        rooms
                .stream()
                .filter(room -> directionGetter.apply(room) > 0)
                .forEach(room -> rooms
                        .stream()
                        .filter(linkedRoom -> linkedRoom.getId() == directionGetter.apply(room))
                        .forEach(linkedRoom -> directionSetter.accept(room, linkedRoom)));
    }

    private Pair<Boolean, Boolean> moveToCardinalDirection(Function<Room, Room> directionGetter) {
        Room room = directionGetter.apply(getCurrentRoom());
        if (room != null) {
            setCurrentRoom(room);
            return new Pair<>(true, false);
        } else {
            return new Pair<>(false, true);
        }
    }
}
