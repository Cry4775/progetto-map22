package component.entity.interfaces;

import engine.MoveInformations.ActionState;

public interface IMovable extends IInteractable {
    public boolean isMoved();

    public void setMoved(boolean moved);

    /**
     * Executes the action "Move".
     * 
     * @return the action state.
     */
    public ActionState move();
}
