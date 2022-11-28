package engine;

import component.entity.doorlike.InvisibleWall;
import component.room.AbstractRoom;

public class MoveInformations {
    public enum ActionState {
        NO_MOVE,
        NORMAL_ACTION
    }

    public enum MovementState {
        POSITION_CHANGED,
        BLOCKED_DOOR,
        BLOCKED_WALL,
        TELEPORT,
        DARK_ROOM,
        NO_ROOM
    }

    private ActionState actionState = null;
    private MovementState movementState = null;
    private boolean actionPerformed = false;
    private InvisibleWall wall;
    private AbstractRoom teleportDestination;

    public void reset() {
        actionState = null;
        movementState = null;
        actionPerformed = false;
        wall = null;
        teleportDestination = null;
    }

    public void setState(ActionState actionState, MovementState movementState) {
        reset();

        if (actionState == null && movementState == null) {
            throw new Error("Move state can't be null for both.");
        }

        this.actionState = actionState;
        this.movementState = movementState;

        if (actionState != null) {
            switch (actionState) {
                case NORMAL_ACTION:
                    actionPerformed = true;
                    break;
                default:
                    break;
            }
        }

        if (movementState != null) {
            switch (movementState) {
                case POSITION_CHANGED:
                    actionPerformed = true;
                    break;
                case TELEPORT:
                    actionPerformed = true;
                    break;
                default:
                    break;
            }
        }
    }

    public ActionState getActionState() {
        return actionState;
    }

    public MovementState getMovementState() {
        return movementState;
    }

    public boolean isActionPerformed() {
        return actionPerformed;
    }

    public InvisibleWall getWall() {
        return wall;
    }

    public void setWall(InvisibleWall wall) {
        this.wall = wall;
    }

    public AbstractRoom getTeleportDestination() {
        return teleportDestination;
    }

    public void setTeleportDestination(AbstractRoom teleportDestination) {
        this.teleportDestination = teleportDestination;
    }
}
