package component.entity.interfaces;

public interface IWearable extends IPickupable {
    public boolean isWorn();

    public void setWorn(boolean worn);

    public boolean wear();

    public boolean unwear();
}
