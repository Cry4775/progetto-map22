/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package di.uniba.map.b.adventure.component.room;

import java.util.ArrayList;
import java.util.List;
import di.uniba.map.b.adventure.component.entity.AbstractEntity;
import di.uniba.map.b.adventure.component.entity.doorlike.InvisibleWall;
import di.uniba.map.b.adventure.engine.CommandType;
import di.uniba.map.b.adventure.type.RoomEvent;

/**
 * @author Pierdamiano Zagaria
 */
public class PlayableRoom extends AbstractRoom {
    private AbstractRoom south;
    private Integer southId;

    private AbstractRoom north;
    private Integer northId;

    private AbstractRoom southWest;
    private Integer southWestId;

    private AbstractRoom northWest;
    private Integer northWestId;

    private AbstractRoom southEast;
    private Integer southEastId;

    private AbstractRoom northEast;
    private Integer northEastId;

    private AbstractRoom east;
    private Integer eastId;

    private AbstractRoom west;
    private Integer westId;

    private AbstractRoom up;
    private Integer upId;

    private AbstractRoom down;
    private Integer downId;

    private final List<AbstractEntity> objects = new ArrayList<>();

    private RoomEvent event;

    private boolean currentlyDark = false;

    private boolean darkByDefault = false;

    public PlayableRoom(int id, String name, String description) {
        super(id, name, description);
    }

    public PlayableRoom(int id, String name, String description, String imgPath) {
        super(id, name, description, imgPath);
    }

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

    public Integer getSouthWestId() {
        return southWestId;
    }

    public Integer getNorthWestId() {
        return northWestId;
    }

    public Integer getSouthEastId() {
        return southEastId;
    }

    public Integer getNorthEastId() {
        return northEastId;
    }

    public Integer getSouthId() {
        return southId;
    }

    public Integer getNorthId() {
        return northId;
    }

    public Integer getWestId() {
        return westId;
    }

    public Integer getEastId() {
        return eastId;
    }

    public List<AbstractEntity> getObjects() {
        return objects;
    }

    public AbstractRoom getUp() {
        return up;
    }

    public Integer getUpId() {
        return upId;
    }

    public AbstractRoom getDown() {
        return down;
    }

    public Integer getDownId() {
        return downId;
    }

    public void setUp(AbstractRoom up) {
        this.up = up;
    }

    public void setDown(AbstractRoom down) {
        this.down = down;
    }

    public void setSouthId(int southId) {
        this.southId = southId;
    }

    public void setNorthId(int northId) {
        this.northId = northId;
    }

    public void setSouthWestId(int southWestId) {
        this.southWestId = southWestId;
    }

    public void setNorthWestId(int northWestId) {
        this.northWestId = northWestId;
    }

    public void setSouthEastId(int southEastId) {
        this.southEastId = southEastId;
    }

    public void setNorthEastId(int northEastId) {
        this.northEastId = northEastId;
    }

    public void setEastId(int eastId) {
        this.eastId = eastId;
    }

    public void setWestId(int westId) {
        this.westId = westId;
    }

    public void setUpId(int upId) {
        this.upId = upId;
    }

    public void setDownId(int downId) {
        this.downId = downId;
    }

    public void setSouthId(Integer southId) {
        this.southId = southId;
    }

    public void setNorthId(Integer northId) {
        this.northId = northId;
    }

    public void setSouthWestId(Integer southWestId) {
        this.southWestId = southWestId;
    }

    public void setNorthWestId(Integer northWestId) {
        this.northWestId = northWestId;
    }

    public void setSouthEastId(Integer southEastId) {
        this.southEastId = southEastId;
    }

    public void setNorthEastId(Integer northEastId) {
        this.northEastId = northEastId;
    }

    public void setEastId(Integer eastId) {
        this.eastId = eastId;
    }

    public void setWestId(Integer westId) {
        this.westId = westId;
    }

    public void setUpId(Integer upId) {
        this.upId = upId;
    }

    public void setDownId(Integer downId) {
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
}
