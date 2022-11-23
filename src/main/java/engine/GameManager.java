package engine;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import component.entity.AbstractEntity;
import component.entity.Entities;
import component.room.AbstractRoom;
import component.room.CutsceneRoom;
import component.room.Rooms;
import engine.command.Command;
import engine.database.DBManager;
import engine.loader.CommandsLoader;
import engine.loader.RoomsLoader;
import engine.loader.RoomsLoader.Mode;
import gui.GUIManager;

public class GameManager {
    private static GameManager instance;

    private final List<AbstractRoom> rooms = new ArrayList<>();
    private final List<Command> commands = new ArrayList<>();
    private final Inventory inventory = new Inventory();

    private AbstractRoom currentRoom;
    private AbstractRoom previousRoom;

    private MoveInformations currentMoveInfos = new MoveInformations();

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

    public MoveInformations getCurrentMoveInfos() {
        return currentMoveInfos;
    }

    public static synchronized MoveInformations getInstanceCurrentMoveInfos() {
        return getInstance().getCurrentMoveInfos();
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

    protected void proceedToNextRoom() {
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

    protected void moveTo(AbstractRoom room) {
        if (room != null) {
            setPreviousRoom(currentRoom);
            setCurrentRoom(room);
            currentMoveInfos.setPositionChanged(true);
        }
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
