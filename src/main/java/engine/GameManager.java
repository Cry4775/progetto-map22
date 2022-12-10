package engine;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntSupplier;
import javax.swing.JOptionPane;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import component.entity.AbstractEntity;
import component.entity.Entities;
import component.room.AbstractRoom;
import component.room.CutsceneRoom;
import component.room.PlayableRoom;
import component.room.Rooms;
import engine.MoveInformations.MovementState;
import engine.command.Command;
import engine.database.DBManager;
import engine.loader.CommandsLoader;
import engine.loader.RoomsLoader;
import engine.loader.RoomsLoader.Mode;

public class GameManager {
    private static GameManager instance;

    private final List<AbstractRoom> rooms = new ArrayList<>();
    private final List<Command> commands = new ArrayList<>();
    private final Inventory inventory = new Inventory();
    private AbstractRoom currentRoom;
    private AbstractRoom previousRoom;

    private MoveInformations currentMoveInfos = new MoveInformations();

    private GameManager() {}

    protected void requireInitialization() {
        if (rooms.isEmpty() || commands.isEmpty())
            throw new IllegalStateException("Game manager is not initialized. Must be initialized first.");
    }

    protected void initialize(IntSupplier loadDataDialog) {
        if (loadDataDialog == null)
            throw new IllegalArgumentException("Invalid IntSupplier for the loading savedata confirmation dialog.");

        Runnable commandsLoader = new CommandsLoader(commands);
        Thread tCommands = new Thread(commandsLoader, "CommandsLoader");
        tCommands.start();

        Runnable roomsLoader;

        if (DBManager.existsSaving()) {
            roomsLoader =
                    new RoomsLoader(this, loadDataDialog.getAsInt() == JOptionPane.YES_OPTION ? Mode.DB : Mode.JSON);
        } else {
            roomsLoader = new RoomsLoader(this, Mode.JSON);
        }

        Thread tRooms = new Thread(roomsLoader, "RoomsLoader");

        tRooms.start();

        try {
            tCommands.join();
            tRooms.join();
        } catch (InterruptedException e) {
            throw new Error(e);
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

    /** ONLY FOR CUTSCENES. */
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

    /**
     * Changes current room to the requested room and
     * updates the previous room.
     * <p>
     * Updates the {@link engine.MoveInformations.MovementState MovementState} to
     * POSITION_CHANGED
     * </p>
     * 
     * @param room the room you want to change to.
     */
    protected void moveTo(AbstractRoom room) {
        if (room != null) {
            setPreviousRoom(currentRoom);
            setCurrentRoom(room);

            currentMoveInfos.setState(MovementState.POSITION_CHANGED);
        }
    }

    /** Processes the current lighting if the current room is a playable room. */
    protected void processCurrentLighting() {
        if (currentRoom instanceof PlayableRoom) {
            PlayableRoom pRoom = (PlayableRoom) currentRoom;
            pRoom.processRoomLighting(getInventory().getObjects());
        }
    }

    /**
     * @return a list of all the rooms, including the inner mutable ones.
     */
    public List<AbstractRoom> listAllRooms() {
        List<AbstractRoom> result = new ArrayList<>();
        result.addAll(Rooms.listAllRooms(rooms));

        return result;
    }

    /**
     * @return a multimap (the same key can contain more values) of all the objects
     *         contained in all the rooms (including the inner mutable ones).
     */
    public Multimap<String, AbstractEntity> mapAllRoomsObjects() {
        Multimap<String, AbstractEntity> result = ArrayListMultimap.create();
        result.putAll(Entities.mapRoomsObjects(rooms));

        return result;
    }
}
