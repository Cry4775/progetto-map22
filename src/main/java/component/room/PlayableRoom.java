package component.room;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import component.entity.AbstractEntity;
import component.entity.Entities;
import component.entity.container.AbstractContainer;
import component.entity.doorlike.InvisibleWall;
import component.entity.interfaces.ILightSource;
import component.event.RoomEvent;
import engine.command.Command.Type;
import engine.database.DBManager;
import gui.GUIManager;

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

    private List<AbstractEntity> objects = new ArrayList<>();
    private RoomEvent event;

    private boolean dark = false;
    private boolean darkByDefault = false;

    public PlayableRoom(ResultSet resultSet) throws SQLException {
        super(resultSet);
        southId = resultSet.getString(7);
        northId = resultSet.getString(8);
        southWestId = resultSet.getString(9);
        northWestId = resultSet.getString(10);
        southEastId = resultSet.getString(11);
        northEastId = resultSet.getString(12);
        eastId = resultSet.getString(13);
        westId = resultSet.getString(14);
        upId = resultSet.getString(15);
        downId = resultSet.getString(16);
        darkByDefault = resultSet.getBoolean(17);
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

    public AbstractRoom getUp() {
        return up;
    }

    public void setUp(AbstractRoom up) {
        this.up = up;
    }

    public AbstractRoom getDown() {
        return down;
    }

    public void setDown(AbstractRoom down) {
        this.down = down;
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

    public String getUpId() {
        return upId;
    }

    public String getDownId() {
        return downId;
    }

    /**
     * @return {@code true} if the room must be dark if there's no light source on,
     *         {@code false} otherwise.
     */
    protected boolean isDarkByDefault() {
        return darkByDefault;
    }

    /**
     * @return {@code true} if the room is currently dark,
     *         {@code false} otherwise.
     */
    public boolean isDark() {
        return dark;
    }

    /**
     * Getter modes:
     * <ul>
     * <li><b>UNPACK_CONTAINERS</b>: gets the list of objects including eventual
     * objects in all the containers.</li>
     * <li><b>INCLUDE_NEW_ROOMS</b>: gets the list of objects including eventual
     * objects in all the child rooms (doesn't unpack containers).</li>
     * <li><b>INCLUDE_EVERYTHING</b>: gets the list of objects including eventual
     * objects in all the child rooms and containers.</li>
     * </ul>
     */
    public enum Mode {
        UNPACK_CONTAINERS,
        INCLUDE_NEW_ROOMS,
        INCLUDE_EVERYTHING
    }

    public List<AbstractEntity> getObjects() {
        if (objects == null)
            objects = new ArrayList<>();
        return objects;
    }

    public List<AbstractEntity> getObjects(Mode mode) {
        List<AbstractEntity> result = new ArrayList<>();

        if (mode == Mode.UNPACK_CONTAINERS) {
            if (objects != null) {
                for (AbstractEntity obj : objects) {
                    result.addAll(AbstractContainer.getAllObjectsInside(obj));
                    result.add(obj);
                }
            }
        } else if (mode == Mode.INCLUDE_NEW_ROOMS) {
            for (PlayableRoom room : Rooms.listCheckedRooms(PlayableRoom.class, Rooms.getAllRooms(this))) {
                result.addAll(room.getObjects());
            }
        } else if (mode == Mode.INCLUDE_EVERYTHING) {
            result.addAll(getObjects(Mode.INCLUDE_NEW_ROOMS));

            List<AbstractEntity> buffer = new ArrayList<>();

            for (AbstractEntity obj : result) {
                buffer.addAll(AbstractContainer.getAllObjectsInside(obj));
            }

            result.addAll(buffer);
        }

        return result;
    }

    /**
     * @param mode the desired mode for getter.
     * @return a multimap (the same key can contain more values) of all the desired objects.
     */
    public Multimap<String, AbstractEntity> getObjectsAsMap(Mode mode) {
        Multimap<String, AbstractEntity> result = ArrayListMultimap.create();

        for (AbstractEntity obj : getObjects(mode)) {
            result.put(obj.getId(), obj);
        }

        return result;
    }

    protected RoomEvent getEvent() {
        return event;
    }

    public void setEvent(RoomEvent event) {
        this.event = event;
    }

    public void removeObject(AbstractEntity obj) {
        objects.remove(obj);
        obj.setParent(null);
        obj.setClosestRoomParent(null);
    }

    /**
     * @param direction the parsed direction command.
     * @return the invisible wall blocking the way, if there's one, {@code null} otherwise.
     */
    public InvisibleWall getInvisibleWall(Type direction) {
        if (objects != null) {
            for (InvisibleWall wall : Entities.listCheckedEntities(InvisibleWall.class, objects)) {
                if (wall.isBlocking(direction)) {
                    return wall;
                }
            }
        }
        return null;
    }

    /**
     * @param direction the parsed direction command.
     * @return the room at the requested direction, if exists.
     */
    public AbstractRoom getRoomAt(Type direction) {
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

    /** Triggers the room event, if it's not dark. */
    public void triggerEvent() {
        if (dark)
            return;

        if (event != null) {
            GUIManager.appendOutput(event.getText());
            event = null;
        }
    }

    /**
     * Processes the current lighting.
     * Checks if this room is dark by default and if it is,
     * searchs the inventory for active light sources.
     * 
     * @param inventory the inventory objects list.
     */
    public void processRoomLighting(List<AbstractEntity> inventory) {
        List<ILightSource> invLightSources = Entities
                .listCheckedInterfaceEntities(ILightSource.class, inventory);

        if (darkByDefault) {
            for (ILightSource lightSource : invLightSources) {
                if (dark) {
                    if (lightSource.isOn()) {
                        dark = false;
                        break;
                    }
                } else {
                    if (!lightSource.isOn()) {
                        dark = true;
                    }
                }
            }
        }
    }

    @Override
    public void saveOnDB() throws SQLException {
        PreparedStatement stm = DBManager.getConnection().prepareStatement(
                "INSERT INTO SAVEDATA.PlayableRoom values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        PreparedStatement evtStm = DBManager.getConnection().prepareStatement(
                "INSERT INTO SAVEDATA.RoomEvent values (?, ?, ?)");

        super.setKnownValuesOnStatement(stm);
        stm.setString(4, getImgPath());
        stm.setBoolean(5, false);
        stm.setString(6, null);
        stm.setString(7, southId);
        stm.setString(8, northId);
        stm.setString(9, southWestId);
        stm.setString(10, northWestId);
        stm.setString(11, southEastId);
        stm.setString(12, northEastId);
        stm.setString(13, eastId);
        stm.setString(14, westId);
        stm.setString(15, upId);
        stm.setString(16, downId);
        stm.setBoolean(17, darkByDefault);
        stm.executeUpdate();

        if (event != null) {
            evtStm.setString(1, getId());
            evtStm.setString(2, event.getEventType().toString());
            evtStm.setString(3, event.getText());
            evtStm.executeUpdate();
        }
    }
}
