package component.entity.interfaces;

import component.entity.AbstractEntity;
import gui.GUIManager;

public interface IFillable extends IInteractable {
    public boolean isFilled();

    public void setFilled(boolean filled);

    public AbstractEntity getEligibleItem();

    public void setEligibleItem(AbstractEntity item);

    public boolean fill(AbstractEntity obj);

    public default void lookAt() {
        if (isFilled()) {
            GUIManager.appendOutput("É pieno di: " + getEligibleItem().getName());
        } else {
            GUIManager.appendOutput("È vuoto.");
        }
    }
}
