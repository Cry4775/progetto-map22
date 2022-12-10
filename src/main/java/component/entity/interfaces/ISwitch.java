package component.entity.interfaces;

import engine.MoveInformations.ActionState;

public interface ISwitch extends IInteractable {
    public boolean isOn();

    public void setOn(boolean on);

    /**
     * Executes the "Turn on" action.
     * 
     * @return the action state.
     */
    public ActionState turnOn();

    /**
     * Executes the "Turn off" action.
     * 
     * @return the action state.
     */
    public ActionState turnOff();
}
