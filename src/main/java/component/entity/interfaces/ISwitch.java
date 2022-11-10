package component.entity.interfaces;

public interface ISwitch extends IInteractable {
    public boolean isOn();

    public void setOn(boolean on);

    public StringBuilder turnOn();

    public StringBuilder turnOff();
}
