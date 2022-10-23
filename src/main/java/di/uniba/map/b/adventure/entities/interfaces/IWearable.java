package di.uniba.map.b.adventure.entities.interfaces;

public interface IWearable extends IPickupable {
    public boolean isWorn();

    public void setWorn(boolean value);

    public StringBuilder wear();

    public StringBuilder unwear();
}
