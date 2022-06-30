/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package di.uniba.map.b.adventure;

import di.uniba.map.b.adventure.parser.ParserOutput;
import di.uniba.map.b.adventure.type.AdvObject;
import di.uniba.map.b.adventure.type.Command;
import di.uniba.map.b.adventure.type.Room;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import java.awt.Color;

/**
 *
 * @author pierpaolo
 */
public abstract class GameDescription {

    private final List<Room> rooms = new ArrayList<>();

    private final List<Command> commands = new ArrayList<>();

    private final List<AdvObject> inventory = new ArrayList<>();

    private Room currentRoom;

    public List<Room> getRooms() {
        return rooms;
    }

    public List<Command> getCommands() {
        return commands;
    }

    public Room getCurrentRoom() {
        return currentRoom;
    }

    public void setCurrentRoom(Room currentRoom) {
        this.currentRoom = currentRoom;
    }

    public List<AdvObject> getInventory() {
        return inventory;
    }

    public void setCompassLabel(Room room, JLabel directionLbl) {
        directionLbl.setForeground(room == null ? Color.RED : Color.GREEN);
    }

    public void setCompassLabels(GameJFrame gui) {
        setCompassLabel(getCurrentRoom().getNorth(), gui.getLblCompassNorthText());
        setCompassLabel(getCurrentRoom().getSouth(), gui.getLblCompassSouthText());
        setCompassLabel(getCurrentRoom().getWest(), gui.getLblCompassWestText());
        setCompassLabel(getCurrentRoom().getEast(), gui.getLblCompassEastText());
        setCompassLabel(getCurrentRoom().getSouthWest(), gui.getLblCompassSouthWestText());
        setCompassLabel(getCurrentRoom().getSouthEast(), gui.getLblCompassSouthEastText());
        setCompassLabel(getCurrentRoom().getNorthEast(), gui.getLblCompassNorthEastText());
        setCompassLabel(getCurrentRoom().getNorthWest(), gui.getLblCompassNorthWestText());
    }

    public abstract void init() throws Exception;

    public abstract void nextMove(ParserOutput p, GameJFrame gui);

}
