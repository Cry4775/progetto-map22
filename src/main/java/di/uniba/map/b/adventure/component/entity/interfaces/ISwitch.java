package di.uniba.map.b.adventure.component.entity.interfaces;

public interface ISwitch {
    public boolean isOn();

    public void setOn(boolean value);

    public StringBuilder turnOn();

    public StringBuilder turnOff();
}
