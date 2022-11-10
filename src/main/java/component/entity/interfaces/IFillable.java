package component.entity.interfaces;

import component.entity.AbstractEntity;
import engine.OutputManager;

public interface IFillable extends IInteractable {
    public boolean isFilled();

    public void setFilled(boolean filled);

    public AbstractEntity getEligibleItem();

    public void setEligibleItem(AbstractEntity item);

    public boolean fill(AbstractEntity obj);

    public default void sendLookMessage() {
        if (isFilled()) {
            OutputManager.append("É pieno di: " + getEligibleItem().getName());
        } else {
            OutputManager.append("È vuoto.");
        }
    }
}
