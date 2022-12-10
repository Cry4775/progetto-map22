package component.event;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import component.entity.AbstractEntity;
import component.entity.container.AbstractContainer;
import component.room.AbstractRoom;
import component.room.MutableRoom;
import component.room.PlayableRoom;
import engine.GameManager;
import engine.MoveInformations;
import engine.MoveInformations.MovementState;
import gui.GUIManager;

public class ObjectEvent extends AbstractEvent {
    private String updateTargetRoomId;
    private MutableRoom updateTargetRoom;

    private boolean updatingParentRoom;

    private String teleportsPlayerToRoomId;
    private AbstractRoom teleportsPlayerToRoom;

    private boolean destroyOnTrigger = false;

    public ObjectEvent(ResultSet resultSet) throws SQLException {
        super(resultSet);
        updatingParentRoom = resultSet.getBoolean(5);
        updateTargetRoomId = resultSet.getString(6);
        teleportsPlayerToRoomId = resultSet.getString(7);
        destroyOnTrigger = resultSet.getBoolean(8);
    }

    /**
     * @return {@code true} if the parent (object) must be destroyed on trigger of the event,
     *         {@code false} otherwise.
     */
    public boolean mustDestroyOnTrigger() {
        return destroyOnTrigger;
    }

    public String getUpdateTargetRoomId() {
        return updateTargetRoomId;
    }

    public MutableRoom getUpdateTargetRoom() {
        return updateTargetRoom;
    }

    public void setUpdateTargetRoom(MutableRoom updateTargetRoom) {
        this.updateTargetRoom = updateTargetRoom;
    }

    public boolean isUpdatingParentRoom() {
        return updatingParentRoom;
    }

    public String getTeleportsPlayerToRoomId() {
        return teleportsPlayerToRoomId;
    }

    public AbstractRoom getTeleportsPlayerToRoom() {
        return teleportsPlayerToRoom;
    }

    public void setTeleportsPlayerToRoom(AbstractRoom teleportsPlayerToRoom) {
        this.teleportsPlayerToRoom = teleportsPlayerToRoom;
    }

    /**
     * Triggers the event.
     * 
     * @param obj the event parent object.
     */
    public void trigger(AbstractEntity obj) {
        if (updatingParentRoom) {
            MutableRoom parentRoom = (MutableRoom) obj.getClosestRoomParent();
            parentRoom.updateToNewRoom();
        }

        if (updateTargetRoom != null) {
            updateTargetRoom.updateToNewRoom();
        }

        if (teleportsPlayerToRoom != null) {
            MoveInformations status = GameManager.getInstanceCurrentMoveInfos();
            status.setState(MovementState.TELEPORT);
            status.setTeleportDestination(teleportsPlayerToRoom);
        }

        if (destroyOnTrigger) {
            if (obj.getParent() instanceof AbstractContainer) {
                AbstractContainer container = (AbstractContainer) obj.getParent();

                container.removeObject(obj);
            } else if (obj.getParent() instanceof PlayableRoom) {
                PlayableRoom pRoom = (PlayableRoom) obj.getParent();

                pRoom.removeObject(obj);
            } else {
                throw new Error(
                        "Couldn't find the parent room of " + obj.getName()
                                + " (" + obj.getId() + ").");
            }
        }

        GUIManager.appendOutput(getText());

        obj.getEvents().remove(this);
    }

    /**
     * @param events the list of object events.
     * @param type the event type.
     * @return the requested event, if any.
     */
    public static ObjectEvent getEvent(List<ObjectEvent> events, EventType type) {
        if (events != null) {
            for (ObjectEvent evt : events) {
                if (evt.getEventType() == type) {
                    return evt;
                }
            }
        }
        return null;
    }

}
