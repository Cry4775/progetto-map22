package component.entity.doorlike;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.google.common.collect.Multimap;
import component.entity.AbstractEntity;
import component.entity.interfaces.IWearable;
import component.room.AbstractRoom;
import component.room.PlayableRoom;
import engine.Inventory;
import engine.command.Command.Type;
import engine.database.DBManager;

public class InvisibleWall extends AbstractEntity {

    private boolean locked = true;

    private String trespassingWhenLockedText;

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

    public InvisibleWall(ResultSet resultSet) throws SQLException {
        super(resultSet);
        locked = resultSet.getBoolean(6);
        blockedRoomId = resultSet.getString(7);
        trespassingWhenLockedText = resultSet.getString(8);
        northBlocked = resultSet.getBoolean(9);
        southBlocked = resultSet.getBoolean(10);
        eastBlocked = resultSet.getBoolean(11);
        westBlocked = resultSet.getBoolean(12);
        northEastBlocked = resultSet.getBoolean(13);
        northWestBlocked = resultSet.getBoolean(14);
        southEastBlocked = resultSet.getBoolean(15);
        southWestBlocked = resultSet.getBoolean(16);
        upBlocked = resultSet.getBoolean(17);
        downBlocked = resultSet.getBoolean(18);
    }

    public String getTrespassingWhenLockedText() {
        return trespassingWhenLockedText;
    }

    @Override
    public void processReferences(Multimap<String, AbstractEntity> objects,
            List<AbstractRoom> rooms) {
        super.processReferences(objects, rooms);

        if (getRequiredWearedItemsIdToInteract() != null) {
            for (String reqId : getRequiredWearedItemsIdToInteract()) {
                if (!objects.containsKey(reqId)) {
                    throw new Error(
                            "Couldn't find the requested \"requiredWearedItemsIdToInteract\" ID "
                                    + "(" + reqId + ") on " + getName()
                                    + " (" + getId()
                                    + "). Check the JSON file for correct object IDs.");
                }

                setRequiredWearedItemsToInteract(new ArrayList<>());

                for (AbstractEntity obj : objects.get(reqId)) {
                    if (obj instanceof IWearable) {
                        getRequiredWearedItemsToInteract().add((IWearable) obj);
                    }
                }
            }
        }
    }

    public void processRequirements() {
        if (locked) {
            if (getRequiredWearedItemsToInteract() != null && !getRequiredWearedItemsToInteract().isEmpty()) {
                for (IWearable wearable : getRequiredWearedItemsToInteract()) {
                    if (!wearable.isWorn()) {
                        return;
                    }
                }
                locked = false;
            }
        } else {
            if (getRequiredWearedItemsToInteract() != null && !getRequiredWearedItemsToInteract().isEmpty()) {
                for (IWearable wearable : getRequiredWearedItemsToInteract()) {
                    if (!wearable.isWorn()) {
                        locked = true;
                        return;
                    }
                }
            }
        }
    }

    public boolean isBlocking(Type direction) {
        PlayableRoom parentRoom = (PlayableRoom) getParent();
        AbstractRoom nextRoom = parentRoom.getRoomAt(direction);
        processRequirements();

        if (locked) {
            if (blockedRoomId != null) {
                if (nextRoom != null && nextRoom.getId().equals(blockedRoomId)) {
                    return true;
                }
            } else {
                // If the wall is blocking a "fake" room we use these booleans
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
        }

        return false;
    }

    @Override
    public void saveOnDB() throws SQLException {
        PreparedStatement stm = DBManager.getConnection().prepareStatement(
                "INSERT INTO SAVEDATA.InvisibleWall values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

        setKnownValuesOnStatement(stm);
        stm.setBoolean(6, locked);
        stm.setString(7, blockedRoomId);
        stm.setString(8, trespassingWhenLockedText);
        stm.setBoolean(9, northBlocked);
        stm.setBoolean(10, southBlocked);
        stm.setBoolean(11, eastBlocked);
        stm.setBoolean(12, westBlocked);
        stm.setBoolean(13, northEastBlocked);
        stm.setBoolean(14, northWestBlocked);
        stm.setBoolean(15, southEastBlocked);
        stm.setBoolean(16, southWestBlocked);
        stm.setBoolean(17, upBlocked);
        stm.setBoolean(18, downBlocked);
        stm.executeUpdate();

        saveExternalsOnDB();
    }

    public static void loadFromDB(List<AbstractRoom> allRooms, Inventory inventory) throws SQLException {
        PreparedStatement stm =
                DBManager.getConnection()
                        .prepareStatement("SELECT * FROM SAVEDATA.InvisibleWall");
        ResultSet resultSet = stm.executeQuery();

        while (resultSet.next()) {
            InvisibleWall obj = new InvisibleWall(resultSet);

            obj.loadLocation(resultSet, allRooms, inventory);
            obj.loadObjEvents();
        }

        stm.close();
    }

}
