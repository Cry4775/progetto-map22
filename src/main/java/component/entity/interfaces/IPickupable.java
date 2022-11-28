package component.entity.interfaces;

import engine.Inventory;
import engine.MoveInformations.ActionState;

public interface IPickupable extends IInteractable {
    public boolean isPickedUp();

    public void setPickedUp(boolean pickedUp);

    public ActionState pickup(Inventory inventory);
}
