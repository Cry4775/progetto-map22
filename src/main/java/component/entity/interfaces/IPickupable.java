package component.entity.interfaces;

public interface IPickupable extends IInteractable {
    public boolean isPickedUp();

    public void setPickedUp(boolean pickedUp);

    public boolean pickup();
}
