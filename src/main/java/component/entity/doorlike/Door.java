package component.entity.doorlike;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import com.google.common.collect.Multimap;
import component.entity.AbstractEntity;
import component.entity.interfaces.IOpenable;
import component.event.EventType;
import component.room.AbstractRoom;
import component.room.PlayableRoom;
import sound.SoundManager;
import sound.SoundManager.Mode;

public class Door extends AbstractEntity implements IOpenable {

    public Door(ResultSet resultSet) throws SQLException {
        super(resultSet);
        open = resultSet.getBoolean(5);
        locked = resultSet.getBoolean(6);
        unlockedWithItemId = resultSet.getString(7);
        blockedRoomId = resultSet.getString(8);
    }

    private boolean open = false;
    private boolean locked = false;
    private AbstractEntity unlockedWithItem;
    private String unlockedWithItemId;
    private String blockedRoomId;
    private AbstractRoom blockedRoom;

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public AbstractEntity getUnlockedWithItem() {
        return unlockedWithItem;
    }

    public void setUnlockedWithItem(AbstractEntity unlockedWithItem) {
        this.unlockedWithItem = unlockedWithItem;
    }

    @Override
    public String getUnlockedWithItemId() {
        return unlockedWithItemId;
    }

    public void setUnlockedWithItemId(String unlockedWithItemId) {
        this.unlockedWithItemId = unlockedWithItemId;
    }

    public String getBlockedRoomId() {
        return blockedRoomId;
    }

    public void setBlockedRoomId(String blockedRoomId) {
        this.blockedRoomId = blockedRoomId;
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
    public StringBuilder open(AbstractEntity key) {
        StringBuilder outString = new StringBuilder();

        boolean unlocked = false;

        if (locked) {
            if (blockedRoom != null) {
                if (unlockedWithItem.equals(key)) {
                    locked = false;
                    unlocked = true;
                    key.setMustDestroyFromInv(true);
                } else {
                    outString.append(key == null ? "É chiusa a chiave." : "Non funziona.");
                    outString.append(processEvent(EventType.OPEN_LOCKED));

                    return outString;
                }
            }
        }

        if (!open) {
            open = true;
            SoundManager.playWav(unlocked ? SoundManager.DOOR_UNLOCK_OPEN_SOUND_PATH
                    : SoundManager.DOOR_OPEN_SOUND_PATH, Mode.SOUND);
            outString.append("Hai aperto: " + getName());
            outString.append(processEvent(EventType.OPEN_UNLOCKED));

            setActionPerformed(true);
        } else {
            outString.append("É giá aperta.");
        }

        return outString;
    }

    public AbstractRoom getBlockedRoom() {
        return blockedRoom;
    }

    public void setBlockedRoom(AbstractRoom blockedRoom) {
        this.blockedRoom = blockedRoom;
    }

    @Override
    public void processReferences(Multimap<String, AbstractEntity> objects,
            List<AbstractRoom> rooms) {
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

        processRoomParent(rooms);
        processEventReferences(objects, rooms);
    }

    @Override
    public void saveOnDB(Connection connection) throws SQLException {
        PreparedStatement stm = connection.prepareStatement(
                "INSERT INTO SAVEDATA.Door values (?, ?, ?, ?, ?, ?, ?, ?)");

        stm.setString(1, getId());
        stm.setString(2, getName());
        stm.setString(3, getDescription());

        if (getParent() instanceof PlayableRoom) {
            stm.setString(4, getClosestRoomParent().getId());
        }

        stm.setBoolean(5, open);
        stm.setBoolean(6, locked);
        stm.setString(7, unlockedWithItemId);
        stm.setString(8, blockedRoomId);
        stm.executeUpdate();

        saveExternalsOnDB(connection);
    }
}