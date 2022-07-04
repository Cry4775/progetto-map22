package di.uniba.map.b.adventure.type;

public class PickupEvent extends AdvEvent {
    private Integer invokerObjectId;
    private Boolean updatingParentRoom;

    public Boolean isUpdatingParentRoom() {
        return updatingParentRoom;
    }

    public void setUpdatingParentRoom(Boolean updatingParentRoom) {
        this.updatingParentRoom = updatingParentRoom;
    }

    public Integer getInvokerObjectId() {
        return invokerObjectId;
    }

    public void setInvokerObjectId(Integer invokerObjectId) {
        this.invokerObjectId = invokerObjectId;
    }
}
