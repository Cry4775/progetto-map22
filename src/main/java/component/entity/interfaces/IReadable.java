package component.entity.interfaces;

import engine.MoveInformations.ActionState;

public interface IReadable extends IInteractable {
    /**
     * Executes the "Read" action.
     * 
     * @return the action state.
     */
    public ActionState read();
}
