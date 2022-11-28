package component.entity.interfaces;

import engine.MoveInformations.ActionState;

public interface IWearable extends IPickupable {
    public boolean isWorn();

    public void setWorn(boolean worn);

    public ActionState wear();

    public ActionState unwear();
}
