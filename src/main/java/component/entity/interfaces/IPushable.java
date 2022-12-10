package component.entity.interfaces;

import engine.MoveInformations.ActionState;

public interface IPushable extends IInteractable {
    public boolean isPushed();

    public void setPushed(boolean pushed);

    /**
     * Executes the "Push" action.
     * 
     * @return the action state.
     */
    public ActionState push();
}
