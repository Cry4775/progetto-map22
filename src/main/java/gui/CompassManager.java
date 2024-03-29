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

/** Manages the compass labels colors. */
public class CompassManager {
    private static JLabel northLbl;
    private static JLabel southLbl;
    private static JLabel eastLbl;
    private static JLabel westLbl;
    private static JLabel northEastLbl;
    private static JLabel northWestLbl;
    private static JLabel southEastLbl;
    private static JLabel southWestLbl;

    private static AbstractRoom currentRoom;
    private static AbstractRoom previousRoom;

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

    /**
     * Checks that this manager objects references are not null.
     * 
     * @throws NullPointerException if any of the GUI direction labels is {@code null}.
     */
    private static void requireNonNullLabels() throws NullPointerException {
        Objects.requireNonNull(northLbl);
        Objects.requireNonNull(southLbl);
        Objects.requireNonNull(eastLbl);
        Objects.requireNonNull(westLbl);
        Objects.requireNonNull(northEastLbl);
        Objects.requireNonNull(northWestLbl);
        Objects.requireNonNull(southEastLbl);
        Objects.requireNonNull(southWestLbl);
    }

    /**
     * Creates a SwingWorker thread that processes the requested room and updates the requested
     * direction JLabel.
     * 
     * @param roomToCheck the room to process at the requested direction.
     * @param directionLbl the correspondent JLabel to update.
     */
    private static void updateCompassLabel(AbstractRoom roomToCheck, JLabel directionLbl) {
        new SwingWorker<Color, Void>() {
            @Override
            protected Color doInBackground() throws Exception {
                if (roomToCheck != null) {
                    if (roomToCheck.equals(previousRoom)) {
                        return Color.BLUE;
                    }

                    if (currentRoom instanceof PlayableRoom) {
                        PlayableRoom pRoom = (PlayableRoom) currentRoom;
                        for (Door door : Entities.listCheckedEntities(Door.class, pRoom.getObjects())) {
                            if (door.getBlockedRoomId().equals(roomToCheck.getId()) && !door.isOpen()) {
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

    /** Resets all the compass labels to red (no room). */
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

    /**
     * Updates all the compass directions labels.
     * 
     * @param currentRoom the current room the player's at.
     * @param previousRoom the previous room the player's been at.
     */
    protected static void updateCompass(AbstractRoom currentRoom, AbstractRoom previousRoom) {
        requireNonNullLabels();
        CompassManager.currentRoom = currentRoom;
        CompassManager.previousRoom = previousRoom;

        if (currentRoom instanceof PlayableRoom) {
            PlayableRoom currentPRoom = (PlayableRoom) currentRoom;

            if (!currentPRoom.isDark()) {
                updateCompassLabel(currentPRoom.getNorth(), northLbl);
                updateCompassLabel(currentPRoom.getSouth(), southLbl);
                updateCompassLabel(currentPRoom.getWest(), westLbl);
                updateCompassLabel(currentPRoom.getEast(), eastLbl);
                updateCompassLabel(currentPRoom.getSouthWest(), southWestLbl);
                updateCompassLabel(currentPRoom.getSouthEast(), southEastLbl);
                updateCompassLabel(currentPRoom.getNorthEast(), northEastLbl);
                updateCompassLabel(currentPRoom.getNorthWest(), northWestLbl);
            } else {
                resetCompass();

                if (previousRoom.equals(currentPRoom.getSouth())) {
                    updateCompassLabel(currentPRoom.getSouth(), southLbl);
                } else if (previousRoom.equals(currentPRoom.getNorth())) {
                    updateCompassLabel(currentPRoom.getNorth(), northLbl);
                } else if (previousRoom.equals(currentPRoom.getEast())) {
                    updateCompassLabel(currentPRoom.getEast(), eastLbl);
                } else if (previousRoom.equals(currentPRoom.getWest())) {
                    updateCompassLabel(currentPRoom.getWest(), westLbl);
                } else if (previousRoom.equals(currentPRoom.getNorthWest())) {
                    updateCompassLabel(currentPRoom.getNorthWest(), northWestLbl);
                } else if (previousRoom.equals(currentPRoom.getNorthEast())) {
                    updateCompassLabel(currentPRoom.getNorthEast(), northEastLbl);
                } else if (previousRoom.equals(currentPRoom.getSouthWest())) {
                    updateCompassLabel(currentPRoom.getSouthWest(), southWestLbl);
                } else if (previousRoom.equals(currentPRoom.getSouthEast())) {
                    updateCompassLabel(currentPRoom.getSouthEast(), southEastLbl);
                }
            }
        } else {
            resetCompass();
        }
    }

}
