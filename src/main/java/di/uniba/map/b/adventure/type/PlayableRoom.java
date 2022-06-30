/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package di.uniba.map.b.adventure.type;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Pierdamiano Zagaria
 */
public class PlayableRoom extends Room {
    private String look;

    private boolean visible = true;

    private Room south;
    private int southId = 0;

    private Room north;
    private int northId = 0;

    private Room southWest;
    private int southWestId = 0;

    private Room northWest;
    private int northWestId = 0;

    private Room southEast;
    private int southEastId = 0;

    private Room northEast;
    private int northEastId = 0;

    private Room east;
    private int eastId = 0;

    private Room west;
    private int westId = 0;

    private final List<AdvObject> objects = new ArrayList<>();

    public PlayableRoom(int id, String name, String description) {
        super(id, name, description);
    }

    public PlayableRoom(int id, String name, String description, String imgPath) {
        super(id, name, description, imgPath);
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
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

    public int getSouthWestId() {
        return southWestId;
    }

    public int getNorthWestId() {
        return northWestId;
    }

    public int getSouthEastId() {
        return southEastId;
    }

    public int getNorthEastId() {
        return northEastId;
    }

    public int getSouthId() {
        return southId;
    }

    public int getNorthId() {
        return northId;
    }

    public int getWestId() {
        return westId;
    }

    public int getEastId() {
        return eastId;
    }

    public List<AdvObject> getObjects() {
        return objects;
    }

    public String getLook() {
        return look;
    }

    public void setLook(String look) {
        this.look = look;
    }
}
