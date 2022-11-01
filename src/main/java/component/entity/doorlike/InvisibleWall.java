package component.entity.doorlike;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import com.google.common.collect.Multimap;
import component.entity.AbstractEntity;
import component.entity.interfaces.IWearable;
import component.room.AbstractRoom;
import component.room.PlayableRoom;
import engine.command.CommandType;

public class InvisibleWall extends AbstractEntity {

    public InvisibleWall(ResultSet resultSet) throws SQLException {
        super(resultSet);
        locked = resultSet.getBoolean(5);
    }

    private boolean locked = true;

    private String blockedRoomId;

    private boolean northBlocked = false;
    private boolean southBlocked = false;
    private boolean eastBlocked = false;
    private boolean westBlocked = false;
    private boolean northEastBlocked = false;
    private boolean northWestBlocked = false;
    private boolean southEastBlocked = false;
    private boolean southWestBlocked = false;
    private boolean upBlocked = false;
    private boolean downBlocked = false;

    private String trespassingWhenLockedText; // TODO é possibile rimuoverlo

    @Override
    public void processReferences(Multimap<String, AbstractEntity> objects,
            List<AbstractRoom> rooms) {
        if (getRequiredWearedItemsIdToInteract() != null) {
            for (String reqId : getRequiredWearedItemsIdToInteract()) {
                if (!objects.containsKey(reqId)) {
                    throw new Error(
                            "Couldn't find the requested \"requiredWearedItemsIdToInteract\" ID "
                                    + "(" + reqId + ") on " + getName()
                                    + " (" + getId()
                                    + "). Check the JSON file for correct object IDs.");
                }

                for (AbstractEntity obj : objects.get(reqId)) {
                    if (obj instanceof IWearable) {
                        getRequiredWearedItemsToInteract().add((IWearable) obj);
                    }
                }
            }
        }

        processRoomParent(rooms);
        processEventReferences(objects, rooms);
    }

    public void processRequirements(List<AbstractEntity> inventory) {
        if (locked) {
            if (getRequiredWearedItemsToInteract() != null) {
                for (IWearable wearable : getRequiredWearedItemsToInteract()) {
                    if (!wearable.isWorn()) {
                        return;
                    }
                }
                locked = false;
            }
        } else {
            if (getRequiredWearedItemsToInteract() != null) {
                for (IWearable wearable : getRequiredWearedItemsToInteract()) {
                    if (!wearable.isWorn()) {
                        locked = true;
                        return;
                    }
                }
            }
        }
    }

    public boolean isBlocking(CommandType direction) {
        PlayableRoom parentRoom = (PlayableRoom) getParent();
        AbstractRoom nextRoom = parentRoom.getRoomAt(direction);

        if (blockedRoomId != null) {
            if (nextRoom != null && nextRoom.getId().equals(blockedRoomId)) {
                return true;
            }
        } else {
            // If the wall is blocking a "fake" room we use those booleans

            switch (direction) {
                case NORTH:
                    return northBlocked;
                case NORTH_EAST:
                    return northEastBlocked;
                case NORTH_WEST:
                    return northWestBlocked;
                case SOUTH:
                    return southBlocked;
                case SOUTH_EAST:
                    return southEastBlocked;
                case SOUTH_WEST:
                    return southWestBlocked;
                case EAST:
                    return eastBlocked;
                case WEST:
                    return westBlocked;
                case UP:
                    return upBlocked;
                case DOWN:
                    return downBlocked;
                default:
                    return false;
            }
        }

        return false;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public String getBlockedRoomId() {
        return blockedRoomId;
    }

    public void setBlockedRoomId(String blockedRoomId) {
        this.blockedRoomId = blockedRoomId;
    }

    public String getTrespassingWhenLockedText() {
        return trespassingWhenLockedText;
    }

    public void setTrespassingWhenLockedText(String trespassingWhenLockedText) {
        this.trespassingWhenLockedText = trespassingWhenLockedText;
    }

    public boolean isNorthBlocked() {
        return northBlocked;
    }

    public void setNorthBlocked(boolean northBlocked) {
        this.northBlocked = northBlocked;
    }

    public boolean isSouthBlocked() {
        return southBlocked;
    }

    public void setSouthBlocked(boolean southBlocked) {
        this.southBlocked = southBlocked;
    }

    public boolean isEastBlocked() {
        return eastBlocked;
    }

    public void setEastBlocked(boolean eastBlocked) {
        this.eastBlocked = eastBlocked;
    }

    public boolean isWestBlocked() {
        return westBlocked;
    }

    public void setWestBlocked(boolean westBlocked) {
        this.westBlocked = westBlocked;
    }

    public boolean isNorthEastBlocked() {
        return northEastBlocked;
    }

    public void setNorthEastBlocked(boolean northEastBlocked) {
        this.northEastBlocked = northEastBlocked;
    }

    public boolean isNorthWestBlocked() {
        return northWestBlocked;
    }

    public void setNorthWestBlocked(boolean northWestBlocked) {
        this.northWestBlocked = northWestBlocked;
    }

    public boolean isSouthEastBlocked() {
        return southEastBlocked;
    }

    public void setSouthEastBlocked(boolean southEastBlocked) {
        this.southEastBlocked = southEastBlocked;
    }

    public boolean isSouthWestBlocked() {
        return southWestBlocked;
    }

    public void setSouthWestBlocked(boolean southWestBlocked) {
        this.southWestBlocked = southWestBlocked;
    }

    public boolean isUpBlocked() {
        return upBlocked;
    }

    public void setUpBlocked(boolean upBlocked) {
        this.upBlocked = upBlocked;
    }

    public boolean isDownBlocked() {
        return downBlocked;
    }

    public void setDownBlocked(boolean downBlocked) {
        this.downBlocked = downBlocked;
    }

    @Override
    public void saveOnDB(Connection connection) throws SQLException {
        PreparedStatement stm = connection.prepareStatement(
                "INSERT INTO SAVEDATA.InvisibleWall values (?, ?, ?, ?, ?)");

        stm.setString(1, getId());
        stm.setString(2, getName());
        stm.setString(3, getDescription());

        if (getParent() instanceof PlayableRoom) {
            stm.setString(4, getParent().getId());
        }

        stm.setBoolean(5, locked);
        stm.executeUpdate();

        saveAliasesOnDB(connection);
        saveEventsOnDB(connection);
    }

}
