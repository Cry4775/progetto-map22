package component.entity.interfaces;

import engine.MoveInformations.ActionState;

public interface IPushable extends IInteractable {
    public boolean isPushed();

    public void setPushed(boolean pushed);

    public ActionState push();
}
