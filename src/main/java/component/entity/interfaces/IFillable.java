package component.entity.interfaces;

import component.entity.AbstractEntity;

public interface IFillable {
    public boolean isFilled();

    public void setFilled(boolean value);

    public AbstractEntity getEligibleItem();

    public void setEligibleItem(AbstractEntity item);

    public boolean fill(AbstractEntity obj);
}