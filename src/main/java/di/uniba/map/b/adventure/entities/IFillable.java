package di.uniba.map.b.adventure.entities;

public interface IFillable {
    public boolean isFilled();

    public void setFilled(boolean value);

    public AbstractEntity getEligibleItem();

    public void setEligibleItem(AbstractEntity item);

    public boolean fill(AbstractEntity obj);
}
