package di.uniba.map.b.adventure.type;

public class ObjEvent extends AdvEvent {
    private boolean updatingAnotherRoom;
    private Integer updateTargetRoomId;
    private MutablePlayableRoom updateTargetRoom;

    private boolean updatingParentRoom;
    private MutablePlayableRoom parentRoom;

    public MutablePlayableRoom getParentRoom() {
        return parentRoom;
    }

    public void setParentRoom(MutablePlayableRoom parentRoom) {
        this.parentRoom = parentRoom;
    }

    public boolean isUpdatingAnotherRoom() {
        return updatingAnotherRoom;
    }

    public void setUpdatingAnotherRoom(boolean updatingAnotherRoom) {
        this.updatingAnotherRoom = updatingAnotherRoom;
    }

    public Integer getUpdateTargetRoomId() {
        return updateTargetRoomId;
    }

    public void setUpdateTargetRoomId(Integer updateTargetRoomId) {
        this.updateTargetRoomId = updateTargetRoomId;
    }

    public MutablePlayableRoom getUpdateTargetRoom() {
        return updateTargetRoom;
    }

    public void setUpdateTargetRoom(MutablePlayableRoom updateTargetRoom) {
        this.updateTargetRoom = updateTargetRoom;
    }

    public boolean isUpdatingParentRoom() {
        return updatingParentRoom;
    }

    public void setUpdatingParentRoom(boolean updatingParentRoom) {
        this.updatingParentRoom = updatingParentRoom;
    }
}
