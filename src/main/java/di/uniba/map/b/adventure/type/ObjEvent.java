package di.uniba.map.b.adventure.type;

public class ObjEvent extends AdvEvent {
    private boolean updatingAnotherRoom = false;
    private Integer updateTargetRoomId;
    private MutablePlayableRoom updateTargetRoom;

    private boolean updatingParentRoom;
    private MutablePlayableRoom parentRoom;

    private Integer teleportsPlayerToRoomId;
    private AbstractRoom teleportsPlayerToRoom;

    private boolean destroyOnTrigger = false;

    public boolean mustDestroyOnTrigger() {
        return destroyOnTrigger;
    }

    public void setDestroyOnTrigger(boolean destroyOnTrigger) {
        this.destroyOnTrigger = destroyOnTrigger;
    }

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

    public Integer getTeleportsPlayerToRoomId() {
        return teleportsPlayerToRoomId;
    }

    public void setTeleportsPlayerToRoomId(Integer teleportsPlayerToRoomId) {
        this.teleportsPlayerToRoomId = teleportsPlayerToRoomId;
    }

    public AbstractRoom getTeleportsPlayerToRoom() {
        return teleportsPlayerToRoom;
    }

    public void setTeleportsPlayerToRoom(AbstractRoom teleportsPlayerToRoom) {
        this.teleportsPlayerToRoom = teleportsPlayerToRoom;
    }
}
