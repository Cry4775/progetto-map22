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
import engine.Status;

public class ObjectEvent extends AbstractEvent {
    private String updateTargetRoomId;
    private MutableRoom updateTargetRoom;

    private boolean updatingParentRoom;
    private MutableRoom parentRoom;

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

    public boolean mustDestroyOnTrigger() {
        return destroyOnTrigger;
    }

    public void setDestroyOnTrigger(boolean destroyOnTrigger) {
        this.destroyOnTrigger = destroyOnTrigger;
    }

    public MutableRoom getParentRoom() {
        return parentRoom;
    }

    public void setParentRoom(MutableRoom parentRoom) {
        this.parentRoom = parentRoom;
    }

    public String getUpdateTargetRoomId() {
        return updateTargetRoomId;
    }

    public void setUpdateTargetRoomId(String updateTargetRoomId) {
        this.updateTargetRoomId = updateTargetRoomId;
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

    public void setUpdatingParentRoom(boolean updatingParentRoom) {
        this.updatingParentRoom = updatingParentRoom;
    }

    public String getTeleportsPlayerToRoomId() {
        return teleportsPlayerToRoomId;
    }

    public void setTeleportsPlayerToRoomId(String teleportsPlayerToRoomId) {
        this.teleportsPlayerToRoomId = teleportsPlayerToRoomId;
    }

    public AbstractRoom getTeleportsPlayerToRoom() {
        return teleportsPlayerToRoom;
    }

    public void setTeleportsPlayerToRoom(AbstractRoom teleportsPlayerToRoom) {
        this.teleportsPlayerToRoom = teleportsPlayerToRoom;
    }

    public StringBuilder trigger(AbstractEntity obj) {
        StringBuilder outString = new StringBuilder();

        if (updatingParentRoom) {
            parentRoom.updateToNewRoom();
        }

        if (updateTargetRoom != null) {
            updateTargetRoom.updateToNewRoom();
        }

        if (teleportsPlayerToRoom != null) {
            Status status = GameManager.getStatus();
            status.setWarp(true);
            status.setWarpDestination(teleportsPlayerToRoom);
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

        if (getText() != null && !getText().isEmpty()) {
            outString.append("\n\n" + getText());
        }

        setTriggered(true);
        obj.getEvents().remove(this);

        return outString;
    }

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
