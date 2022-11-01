package component.event;

import java.sql.ResultSet;
import java.sql.SQLException;
import component.room.AbstractRoom;
import component.room.MutableRoom;

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
        updatingParentRoom = resultSet.getBoolean(4);
        updateTargetRoomId = resultSet.getString(5);
        teleportsPlayerToRoomId = resultSet.getString(6);
        destroyOnTrigger = resultSet.getBoolean(7);
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
}
