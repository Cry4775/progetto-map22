package di.uniba.map.b.adventure.games;

import di.uniba.map.b.adventure.type.Room;

public class Status {
    private boolean actionPerformed = false;
    private boolean movementAttempt = false;
    private boolean positionChanged = false;
    private boolean roomBlockedByDoor = false;

    private boolean warp = false;
    private Room warpDestination;

    public void reset() {
        actionPerformed = false;
        movementAttempt = false;
        positionChanged = false;
        roomBlockedByDoor = false;
        warp = false;
        warpDestination = null;
    }

    public boolean isroomBlockedByDoor() {
        return roomBlockedByDoor;
    }

    public void setroomBlockedByDoor(boolean roomBlockedByDoor) {
        this.roomBlockedByDoor = roomBlockedByDoor;
    }

    public boolean isActionPerformed() {
        return actionPerformed;
    }

    public void setActionPerformed(boolean actionPerformed) {
        this.actionPerformed = actionPerformed;
    }

    public boolean isMovementAttempt() {
        return movementAttempt;
    }

    public void setMovementAttempt(boolean movementAttempt) {
        this.movementAttempt = movementAttempt;
    }

    public boolean isPositionChanged() {
        return positionChanged;
    }

    public void setPositionChanged(boolean positionChanged) {
        if (positionChanged) {
            this.movementAttempt = true;
            this.positionChanged = true;
            this.roomBlockedByDoor = false;
            this.actionPerformed = true;
        } else {
            this.movementAttempt = true;
            this.positionChanged = false;
            this.roomBlockedByDoor = false;
            this.actionPerformed = false;
        }
    }

    public void setPositionChanged(boolean positionChanged, boolean roomBlockedByDoor) {
        if (positionChanged && !roomBlockedByDoor) {
            setPositionChanged(true);
        } else if (!positionChanged && !roomBlockedByDoor) {
            setPositionChanged(false);
        } else if (!positionChanged && roomBlockedByDoor) {
            setPositionChanged(false);
            this.roomBlockedByDoor = true;
        }
    }

    public boolean isWarp() {
        return warp;
    }

    public void setWarp(boolean warp) {
        this.warp = warp;
    }

    public Room getWarpDestination() {
        return warpDestination;
    }

    public void setWarpDestination(Room warpDestination) {
        this.warpDestination = warpDestination;
    }
}
