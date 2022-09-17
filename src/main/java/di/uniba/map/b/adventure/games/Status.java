package di.uniba.map.b.adventure.games;

import di.uniba.map.b.adventure.type.AbstractRoom;

public class Status {
    private boolean movementAttempt = false;
    private boolean positionChanged = false;
    private boolean roomBlockedByDoor = false;

    private boolean warp = false;
    private AbstractRoom warpDestination;

    public void reset() {
        movementAttempt = false;
        positionChanged = false;
        roomBlockedByDoor = false;
        warp = false;
        warpDestination = null;
    }

    public boolean isRoomBlockedByDoor() {
        return roomBlockedByDoor;
    }

    public void setRoomBlockedByDoor(boolean roomBlockedByDoor) {
        this.roomBlockedByDoor = roomBlockedByDoor;
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
        } else {
            this.movementAttempt = true;
            this.positionChanged = false;
            this.roomBlockedByDoor = false;
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

    public AbstractRoom getWarpDestination() {
        return warpDestination;
    }

    public void setWarpDestination(AbstractRoom warpDestination) {
        this.warpDestination = warpDestination;
    }
}
