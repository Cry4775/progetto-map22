package component.entity.interfaces;

import engine.MoveInformations.ActionState;

public interface IMovable extends IInteractable {
    public boolean isMoved();

    public void setMoved(boolean moved);

    public ActionState move();
}
