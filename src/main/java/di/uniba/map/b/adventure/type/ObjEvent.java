package di.uniba.map.b.adventure.type;

public class ObjEvent extends AdvEvent {
    private boolean updatingAnotherRoom;
    private Integer updateTargetRoomId;
    private MutablePlayableRoom updateTargetRoom;

    private boolean updatingParentRoom;

    private AdvItem neededItem;
    private Integer neededItemId;

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

    public AdvItem getNeededItem() {
        return neededItem;
    }

    public void setNeededItem(AdvItem neededItem) {
        this.neededItem = neededItem;
    }

    public Integer getNeededItemId() {
        return neededItemId;
    }

    public void setNeededItemId(Integer neededItemId) {
        this.neededItemId = neededItemId;
    }
}
