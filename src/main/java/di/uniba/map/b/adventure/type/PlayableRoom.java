/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package di.uniba.map.b.adventure.type;

import java.util.ArrayList;
import java.util.List;
import di.uniba.map.b.adventure.entities.AbstractEntity;

/**
 * @author Pierdamiano Zagaria
 */
public class PlayableRoom extends Room {
    private Room south;
    private Integer southId;

    private Room north;
    private Integer northId;

    private Room southWest;
    private Integer southWestId;

    private Room northWest;
    private Integer northWestId;

    private Room southEast;
    private Integer southEastId;

    private Room northEast;
    private Integer northEastId;

    private Room east;
    private Integer eastId;

    private Room west;
    private Integer westId;

    private Room up;
    private Integer upId;

    private Room down;
    private Integer downId;

    private final List<AbstractEntity> objects = new ArrayList<>();

    private RoomEvent event;

    private boolean dark = false;

    public PlayableRoom(int id, String name, String description) {
        super(id, name, description);
    }

    public PlayableRoom(int id, String name, String description, String imgPath) {
        super(id, name, description, imgPath);
    }

    public boolean isDark() {
        return dark;
    }

    public void setDark(boolean dark) {
        this.dark = dark;
    }

    public Room getSouth() {
        return south;
    }

    public void setSouth(Room south) {
        this.south = south;
    }

    public Room getNorth() {
        return north;
    }

    public void setNorth(Room north) {
        this.north = north;
    }

    public Room getEast() {
        return east;
    }

    public void setEast(Room east) {
        this.east = east;
    }

    public Room getWest() {
        return west;
    }

    public void setWest(Room west) {
        this.west = west;
    }

    public Room getSouthWest() {
        return southWest;
    }

    public void setSouthWest(Room southWest) {
        this.southWest = southWest;
    }

    public Room getNorthWest() {
        return northWest;
    }

    public void setNorthWest(Room northWest) {
        this.northWest = northWest;
    }

    public Room getSouthEast() {
        return southEast;
    }

    public void setSouthEast(Room southEast) {
        this.southEast = southEast;
    }

    public Room getNorthEast() {
        return northEast;
    }

    public void setNorthEast(Room northEast) {
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

    public Room getUp() {
        return up;
    }

    public Integer getUpId() {
        return upId;
    }

    public Room getDown() {
        return down;
    }

    public Integer getDownId() {
        return downId;
    }

    public void setUp(Room up) {
        this.up = up;
    }

    public void setDown(Room down) {
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
}
