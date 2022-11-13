package gui;

import java.awt.Color;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import javax.swing.JLabel;
import javax.swing.SwingWorker;
import component.entity.Entities;
import component.entity.doorlike.Door;
import component.room.AbstractRoom;
import component.room.PlayableRoom;
import engine.GameManager;

public class CompassManager {
    private static JLabel northLbl;
    private static JLabel southLbl;
    private static JLabel eastLbl;
    private static JLabel westLbl;
    private static JLabel northEastLbl;
    private static JLabel northWestLbl;
    private static JLabel southEastLbl;
    private static JLabel southWestLbl;

    protected static void registerGUI(MainFrame mainFrame) {
        Objects.requireNonNull(mainFrame);

        northLbl = mainFrame.getLblCompassNorthText();
        southLbl = mainFrame.getLblCompassSouthText();
        eastLbl = mainFrame.getLblCompassEastText();
        westLbl = mainFrame.getLblCompassWestText();
        northEastLbl = mainFrame.getLblCompassNorthEastText();
        northWestLbl = mainFrame.getLblCompassNorthWestText();
        southEastLbl = mainFrame.getLblCompassSouthEastText();
        southWestLbl = mainFrame.getLblCompassSouthWestText();
    }

    private static void requireNonNullLabels() {
        Objects.requireNonNull(northLbl);
        Objects.requireNonNull(southLbl);
        Objects.requireNonNull(eastLbl);
        Objects.requireNonNull(westLbl);
        Objects.requireNonNull(northEastLbl);
        Objects.requireNonNull(northWestLbl);
        Objects.requireNonNull(southEastLbl);
        Objects.requireNonNull(southWestLbl);
    }

    private static void updateCompassLabel(AbstractRoom room, JLabel directionLbl) {
        new SwingWorker<Color, Void>() {
            @Override
            protected Color doInBackground() throws Exception {
                if (room != null) {
                    if (room.equals(GameManager.getPreviousRoom())) {
                        return Color.BLUE;
                    }

                    if (GameManager.getCurrentRoom() instanceof PlayableRoom) {
                        PlayableRoom pRoom = (PlayableRoom) GameManager.getCurrentRoom();
                        for (Door door : Entities.listCheckedEntities(Door.class, pRoom.getObjects())) {
                            if (door.getBlockedRoomId().equals(room.getId()) && !door.isOpen()) {
                                return Color.ORANGE;
                            }
                        }
                    }

                    return Color.GREEN;
                }

                return Color.RED;
            }

            @Override
            protected void done() {
                try {
                    directionLbl.setForeground(get());
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    private static void resetCompass() {
        requireNonNullLabels();

        updateCompassLabel(null, northLbl);
        updateCompassLabel(null, southLbl);
        updateCompassLabel(null, westLbl);
        updateCompassLabel(null, eastLbl);
        updateCompassLabel(null, southWestLbl);
        updateCompassLabel(null, southEastLbl);
        updateCompassLabel(null, northEastLbl);
        updateCompassLabel(null, northWestLbl);
    }

    protected static void updateCompass() {
        requireNonNullLabels();

        if (GameManager.getCurrentRoom() instanceof PlayableRoom) {
            PlayableRoom currentPlayableRoom = (PlayableRoom) GameManager.getCurrentRoom();

            if (!currentPlayableRoom.isCurrentlyDark()) {
                updateCompassLabel(currentPlayableRoom.getNorth(), northLbl);
                updateCompassLabel(currentPlayableRoom.getSouth(), southLbl);
                updateCompassLabel(currentPlayableRoom.getWest(), westLbl);
                updateCompassLabel(currentPlayableRoom.getEast(), eastLbl);
                updateCompassLabel(currentPlayableRoom.getSouthWest(), southWestLbl);
                updateCompassLabel(currentPlayableRoom.getSouthEast(), southEastLbl);
                updateCompassLabel(currentPlayableRoom.getNorthEast(), northEastLbl);
                updateCompassLabel(currentPlayableRoom.getNorthWest(), northWestLbl);
            } else {
                resetCompass();
                AbstractRoom previousRoom = GameManager.getPreviousRoom();

                if (previousRoom.equals(currentPlayableRoom.getSouth())) {
                    updateCompassLabel(currentPlayableRoom.getSouth(), southLbl);
                } else if (previousRoom.equals(currentPlayableRoom.getNorth())) {
                    updateCompassLabel(currentPlayableRoom.getNorth(), northLbl);
                } else if (previousRoom.equals(currentPlayableRoom.getEast())) {
                    updateCompassLabel(currentPlayableRoom.getEast(), eastLbl);
                } else if (previousRoom.equals(currentPlayableRoom.getWest())) {
                    updateCompassLabel(currentPlayableRoom.getWest(), westLbl);
                } else if (previousRoom.equals(currentPlayableRoom.getNorthWest())) {
                    updateCompassLabel(currentPlayableRoom.getNorthWest(), northWestLbl);
                } else if (previousRoom.equals(currentPlayableRoom.getNorthEast())) {
                    updateCompassLabel(currentPlayableRoom.getNorthEast(), northEastLbl);
                } else if (previousRoom.equals(currentPlayableRoom.getSouthWest())) {
                    updateCompassLabel(currentPlayableRoom.getSouthWest(), southWestLbl);
                } else if (previousRoom.equals(currentPlayableRoom.getSouthEast())) {
                    updateCompassLabel(currentPlayableRoom.getSouthEast(), southEastLbl);
                }
            }
        } else {
            resetCompass();
        }
    }

}
