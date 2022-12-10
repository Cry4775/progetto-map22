package component.entity.interfaces;

import engine.MoveInformations.ActionState;

public interface IPullable extends IInteractable {
    public boolean isPulled();

    public void setPulled(boolean pulled);

    /**
     * Executes the "Pull" action.
     * 
     * @return the action state.
     */
    public ActionState pull();
}
