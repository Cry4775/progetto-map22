package component.room;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import component.entity.AbstractEntity;
import component.entity.doorlike.InvisibleWall;
import component.event.RoomEvent;
import engine.command.CommandType;

/**
 * @author Pierdamiano Zagaria
 */
public class PlayableRoom extends AbstractRoom {
    private AbstractRoom south;
    private String southId;

    private AbstractRoom north;
    private String northId;

    private AbstractRoom southWest;
    private String southWestId;

    private AbstractRoom northWest;
    private String northWestId;

    private AbstractRoom southEast;
    private String southEastId;

    private AbstractRoom northEast;
    private String northEastId;

    private AbstractRoom east;
    private String eastId;

    private AbstractRoom west;
    private String westId;

    private AbstractRoom up;
    private String upId;

    private AbstractRoom down;
    private String downId;

    private final List<AbstractEntity> objects = new ArrayList<>();

    private RoomEvent event;

    private boolean currentlyDark = false;

    private boolean darkByDefault = false;

    public boolean isDarkByDefault() {
        return darkByDefault;
    }

    public void setDarkByDefault(boolean darkByDefault) {
        this.darkByDefault = darkByDefault;
    }

    public boolean isCurrentlyDark() {
        return currentlyDark;
    }

    public void setCurrentlyDark(boolean currentlyDark) {
        this.currentlyDark = currentlyDark;
    }

    public AbstractRoom getSouth() {
        return south;
    }

    public void setSouth(AbstractRoom south) {
        this.south = south;
    }

    public AbstractRoom getNorth() {
        return north;
    }

    public void setNorth(AbstractRoom north) {
        this.north = north;
    }

    public AbstractRoom getEast() {
        return east;
    }

    public void setEast(AbstractRoom east) {
        this.east = east;
    }

    public AbstractRoom getWest() {
        return west;
    }

    public void setWest(AbstractRoom west) {
        this.west = west;
    }

    public AbstractRoom getSouthWest() {
        return southWest;
    }

    public void setSouthWest(AbstractRoom southWest) {
        this.southWest = southWest;
    }

    public AbstractRoom getNorthWest() {
        return northWest;
    }

    public void setNorthWest(AbstractRoom northWest) {
        this.northWest = northWest;
    }

    public AbstractRoom getSouthEast() {
        return southEast;
    }

    public void setSouthEast(AbstractRoom southEast) {
        this.southEast = southEast;
    }

    public AbstractRoom getNorthEast() {
        return northEast;
    }

    public void setNorthEast(AbstractRoom northEast) {
        this.northEast = northEast;
    }

    public String getSouthWestId() {
        return southWestId;
    }

    public String getNorthWestId() {
        return northWestId;
    }

    public String getSouthEastId() {
        return southEastId;
    }

    public String getNorthEastId() {
        return northEastId;
    }

    public String getSouthId() {
        return southId;
    }

    public String getNorthId() {
        return northId;
    }

    public String getWestId() {
        return westId;
    }

    public String getEastId() {
        return eastId;
    }

    public List<AbstractEntity> getObjects() {
        return objects;
    }

    public AbstractRoom getUp() {
        return up;
    }

    public String getUpId() {
        return upId;
    }

    public AbstractRoom getDown() {
        return down;
    }

    public String getDownId() {
        return downId;
    }

    public void setUp(AbstractRoom up) {
        this.up = up;
    }

    public void setDown(AbstractRoom down) {
        this.down = down;
    }

    public void setSouthId(String southId) {
        this.southId = southId;
    }

    public void setNorthId(String northId) {
        this.northId = northId;
    }

    public void setSouthWestId(String southWestId) {
        this.southWestId = southWestId;
    }

    public void setNorthWestId(String northWestId) {
        this.northWestId = northWestId;
    }

    public void setSouthEastId(String southEastId) {
        this.southEastId = southEastId;
    }

    public void setNorthEastId(String northEastId) {
        this.northEastId = northEastId;
    }

    public void setEastId(String eastId) {
        this.eastId = eastId;
    }

    public void setWestId(String westId) {
        this.westId = westId;
    }

    public void setUpId(String upId) {
        this.upId = upId;
    }

    public void setDownId(String downId) {
        this.downId = downId;
    }

    public RoomEvent getEvent() {
        return event;
    }

    public void removeObject(AbstractEntity obj) {
        objects.remove(obj);
    }

    public InvisibleWall getMagicWall(CommandType direction) {
        if (objects != null) {
            for (AbstractEntity obj : objects) {
                if (obj instanceof InvisibleWall) {
                    InvisibleWall wall = (InvisibleWall) obj;

                    if (wall.isBlocking(direction)) {
                        return wall;
                    }
                }
            }
        }
        return null;
    }

    public AbstractRoom getRoomAt(CommandType direction) {
        switch (direction) {
            case NORTH:
                return north;
            case SOUTH:
                return south;
            case EAST:
                return east;
            case WEST:
                return west;
            case NORTH_EAST:
                return northEast;
            case NORTH_WEST:
                return northWest;
            case SOUTH_EAST:
                return southEast;
            case SOUTH_WEST:
                return southWest;
            case UP:
                return up;
            case DOWN:
                return down;
            default:
                return null;
        }
    }

    @Override
    public void saveOnDB(Connection connection) throws SQLException {
        PreparedStatement stm = connection.prepareStatement(
                "INSERT INTO SAVEDATA.PlayableRoom values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        PreparedStatement evtStm = connection.prepareStatement(
                "INSERT INTO SAVEDATA.RoomEvent values (?, ?, ?)");

        stm.setString(1, getId());
        stm.setBoolean(2, false);
        stm.setString(3, null);
        stm.setString(4, southId);
        stm.setString(5, northId);
        stm.setString(6, southWestId);
        stm.setString(7, northWestId);
        stm.setString(8, southEastId);
        stm.setString(9, northEastId);
        stm.setString(10, eastId);
        stm.setString(11, westId);
        stm.setString(12, upId);
        stm.setString(13, downId);
        stm.setBoolean(14, currentlyDark);
        stm.executeUpdate();

        if (event != null) {
            evtStm.setString(1, getId());
            evtStm.setString(2, event.getEventType().toString());
            evtStm.setString(3, event.getText());
            evtStm.executeUpdate();
        }
    }
}
