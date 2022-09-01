/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package di.uniba.map.b.adventure;

import di.uniba.map.b.adventure.entities.AbstractEntity;
import di.uniba.map.b.adventure.parser.ParserOutput;
import di.uniba.map.b.adventure.type.Command;
import di.uniba.map.b.adventure.type.PlayableRoom;
import di.uniba.map.b.adventure.type.Room;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import java.awt.Color;
import java.io.IOException;

/**
 * @author pierpaolo
 */
public abstract class GameDescription {

    private final List<Room> rooms = new ArrayList<>();

    private final List<Command> commands = new ArrayList<>();

    private final List<AbstractEntity> inventory = new ArrayList<>();

    private Room currentRoom;

    private Room previousRoom;

    public Room getPreviousRoom() {
        return previousRoom;
    }

    public void setPreviousRoom(Room previousRoom) {
        this.previousRoom = previousRoom;
    }

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

    public List<AbstractEntity> getInventory() {
        return inventory;
    }

    public void setCompassLabel(Room room, JLabel directionLbl) {
        directionLbl.setForeground(room == null ? Color.RED : Color.GREEN);
    }

    private void clearCompassLabels(GameJFrame gui) {
        setCompassLabel(null, gui.getLblCompassNorthText());
        setCompassLabel(null, gui.getLblCompassSouthText());
        setCompassLabel(null, gui.getLblCompassWestText());
        setCompassLabel(null, gui.getLblCompassEastText());
        setCompassLabel(null, gui.getLblCompassSouthWestText());
        setCompassLabel(null, gui.getLblCompassSouthEastText());
        setCompassLabel(null, gui.getLblCompassNorthEastText());
        setCompassLabel(null, gui.getLblCompassNorthWestText());
    }

    public void setCompassLabels(GameJFrame gui) {
        if (getCurrentRoom() instanceof PlayableRoom) {
            PlayableRoom currentPlayableRoom = (PlayableRoom) getCurrentRoom();
            if (!currentPlayableRoom.isDark()) {
                setCompassLabel(currentPlayableRoom.getNorth(), gui.getLblCompassNorthText());
                setCompassLabel(currentPlayableRoom.getSouth(), gui.getLblCompassSouthText());
                setCompassLabel(currentPlayableRoom.getWest(), gui.getLblCompassWestText());
                setCompassLabel(currentPlayableRoom.getEast(), gui.getLblCompassEastText());
                setCompassLabel(currentPlayableRoom.getSouthWest(),
                        gui.getLblCompassSouthWestText());
                setCompassLabel(currentPlayableRoom.getSouthEast(),
                        gui.getLblCompassSouthEastText());
                setCompassLabel(currentPlayableRoom.getNorthEast(),
                        gui.getLblCompassNorthEastText());
                setCompassLabel(currentPlayableRoom.getNorthWest(),
                        gui.getLblCompassNorthWestText());
            } else {
                clearCompassLabels(gui);

                if (previousRoom.equals(currentPlayableRoom.getSouth())) {
                    setCompassLabel(currentPlayableRoom.getSouth(), gui.getLblCompassSouthText());
                } else if (previousRoom.equals(currentPlayableRoom.getNorth())) {
                    setCompassLabel(currentPlayableRoom.getNorth(), gui.getLblCompassNorthText());
                } else if (previousRoom.equals(currentPlayableRoom.getEast())) {
                    setCompassLabel(currentPlayableRoom.getEast(), gui.getLblCompassEastText());
                } else if (previousRoom.equals(currentPlayableRoom.getWest())) {
                    setCompassLabel(currentPlayableRoom.getWest(), gui.getLblCompassWestText());
                } else if (previousRoom.equals(currentPlayableRoom.getNorthWest())) {
                    setCompassLabel(currentPlayableRoom.getNorthWest(),
                            gui.getLblCompassNorthWestText());
                } else if (previousRoom.equals(currentPlayableRoom.getNorthEast())) {
                    setCompassLabel(currentPlayableRoom.getNorthEast(),
                            gui.getLblCompassNorthEastText());
                } else if (previousRoom.equals(currentPlayableRoom.getSouthWest())) {
                    setCompassLabel(currentPlayableRoom.getSouthWest(),
                            gui.getLblCompassSouthWestText());
                } else if (previousRoom.equals(currentPlayableRoom.getSouthEast())) {
                    setCompassLabel(currentPlayableRoom.getSouthEast(),
                            gui.getLblCompassSouthEastText());
                }
            }
        }
    }

    public abstract void init() throws IOException;

    public abstract void nextMove(ParserOutput p, GameJFrame gui);

}
