package component.entity.doorlike;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import com.google.common.collect.Multimap;
import component.entity.AbstractEntity;
import component.entity.interfaces.IOpenable;
import component.event.EventType;
import component.room.AbstractRoom;
import engine.database.DBManager;
import gui.GUIManager;
import sound.SoundManager;
import sound.SoundManager.Mode;

public class Door extends AbstractEntity implements IOpenable {

    private boolean open = false;
    private boolean locked = false;
    private AbstractEntity unlockedWithItem;
    private String unlockedWithItemId;
    private String blockedRoomId;
    private AbstractRoom blockedRoom;

    public Door(ResultSet resultSet) throws SQLException {
        super(resultSet);
        open = resultSet.getBoolean(6);
        locked = resultSet.getBoolean(7);
        unlockedWithItemId = resultSet.getString(8);
        blockedRoomId = resultSet.getString(9);
    }

    @Override
    public boolean isOpen() {
        return open;
    }

    @Override
    public void setOpen(boolean open) {
        this.open = open;
    }

    @Override
    public String getUnlockedWithItemId() {
        return unlockedWithItemId;
    }

    @Override
    public boolean isLocked() {
        return locked;
    }

    @Override
    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    @Override
    public void lookAt() {
        super.lookAt();
        IOpenable.super.lookAt();
    }

    public String getBlockedRoomId() {
        return blockedRoomId;
    }

    @Override
    public boolean open(AbstractEntity key) {
        boolean unlocked = false;

        if (!canInteract())
            return false;

        if (locked) {
            if (blockedRoom != null) {
                if (unlockedWithItem.equals(key)) {
                    locked = false;
                    unlocked = true;
                    key.setMustDestroyFromInv(true);
                    unlockedWithItemId = null;
                    unlockedWithItem = null;
                } else {
                    GUIManager.appendOutput(key == null ? "É chiusa a chiave." : "Non funziona.");
                    triggerEvent(EventType.OPEN_LOCKED);

                    return false;
                }
            }
        }

        if (!open) {
            open = true;
            SoundManager.playWav(unlocked ? SoundManager.DOOR_UNLOCK_OPEN_SOUND_PATH
                    : SoundManager.DOOR_OPEN_SOUND_PATH, Mode.SOUND);

            GUIManager.appendOutput("Hai aperto: " + getName());
            triggerEvent(EventType.OPEN_UNLOCKED);
            return true;
        } else {
            GUIManager.appendOutput("É giá aperta.");
        }

        return false;
    }

    @Override
    public void processReferences(Multimap<String, AbstractEntity> objects,
            List<AbstractRoom> rooms) {
        super.processReferences(objects, rooms);

        if (blockedRoomId != null) {
            rooms.stream()
                    .filter(room -> blockedRoomId.equals(room.getId()))
                    .forEach(room -> blockedRoom = room);
        }

        if (unlockedWithItemId != null) {
            if (!objects.containsKey(unlockedWithItemId)) {
                throw new Error(
                        "Couldn't find the requested \"unlockedWithItem\" ID on " + getName()
                                + " (" + getId()
                                + "). Check the JSON file for correct object IDs.");
            }

            for (AbstractEntity reqItem : objects.get(unlockedWithItemId)) {
                unlockedWithItem = reqItem;
            }
        }
    }

    @Override
    public void saveOnDB() throws SQLException {
        PreparedStatement stm = DBManager.getConnection().prepareStatement(
                "INSERT INTO SAVEDATA.Door values (?, ?, ?, ?, ?, ?, ?, ?, ?)");

        setKnownValuesOnStatement(stm);
        stm.setBoolean(6, open);
        stm.setBoolean(7, locked);
        stm.setString(8, unlockedWithItemId);
        stm.setString(9, blockedRoomId);
        stm.executeUpdate();

        saveExternalsOnDB();
    }

    public static void loadFromDB(List<AbstractRoom> allRooms) throws SQLException {
        PreparedStatement stm =
                DBManager.getConnection()
                        .prepareStatement("SELECT * FROM SAVEDATA.Door");
        ResultSet resultSet = stm.executeQuery();

        while (resultSet.next()) {
            Door obj = new Door(resultSet);

            obj.loadLocation(resultSet, allRooms);
            obj.loadObjEvents();
        }

        stm.close();
    }
}
