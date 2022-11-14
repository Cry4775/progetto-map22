package component.entity.interfaces;

import engine.Inventory;

public interface IPickupable extends IInteractable {
    public boolean isPickedUp();

    public void setPickedUp(boolean pickedUp);

    public boolean pickup(Inventory inventory);
}
