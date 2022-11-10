package component.entity.interfaces;

import component.entity.AbstractEntity;

public interface IFillable extends IInteractable {
    public boolean isFilled();

    public void setFilled(boolean filled);

    public AbstractEntity getEligibleItem();

    public void setEligibleItem(AbstractEntity item);

    public boolean fill(AbstractEntity obj);
}
