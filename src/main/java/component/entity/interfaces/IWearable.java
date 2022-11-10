package component.entity.interfaces;

public interface IWearable extends IPickupable {
    public boolean isWorn();

    public void setWorn(boolean worn);

    public void wear();

    public void unwear();
}
