package component.entity.interfaces;

import engine.MoveInformations.ActionState;

public interface ITalkable extends IInteractable {
    /**
     * Executes the "Talk" action.
     * 
     * @return the action state.
     */
    public ActionState talk();
}
