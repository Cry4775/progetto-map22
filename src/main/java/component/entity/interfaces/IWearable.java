package component.entity.interfaces;

import engine.MoveInformations.ActionState;

public interface IWearable extends IPickupable {
    public boolean isWorn();

    public void setWorn(boolean worn);

    /**
     * Executes the "Wear" action.
     * 
     * @return the action state.
     */
    public ActionState wear();

    /**
     * Executes the "Unwear" action.
     * 
     * @return the action state.
     */
    public ActionState unwear();
}
