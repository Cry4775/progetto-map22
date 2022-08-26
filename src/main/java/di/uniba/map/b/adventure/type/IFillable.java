package di.uniba.map.b.adventure.type;

public interface IFillable {
    public boolean isFilled();

    public void setFilled(boolean value);

    public AdvObject getEligibleItem();

    public void setEligibleItem(AdvObject item);

    public boolean fill(AdvObject obj);
}
