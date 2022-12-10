package component.entity.interfaces;

import engine.Inventory;
import engine.MoveInformations.ActionState;

public interface IPickupable extends IInteractable {
    public boolean isPickedUp();

    public void setPickedUp(boolean pickedUp);

    /**
     * Executes the "Pickup" action.
     * 
     * @param inventory the inventory reference.
     * @return the action state.
     */
    public ActionState pickup(Inventory inventory);
}
