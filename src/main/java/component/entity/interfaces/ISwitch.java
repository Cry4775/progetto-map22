package component.entity.interfaces;

import engine.MoveInformations.ActionState;

public interface ISwitch extends IInteractable {
    public boolean isOn();

    public void setOn(boolean on);

    public ActionState turnOn();

    public ActionState turnOff();
}
