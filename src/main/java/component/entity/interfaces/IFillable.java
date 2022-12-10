package component.entity.interfaces;

import component.entity.AbstractEntity;
import gui.GUIManager;

public interface IFillable extends IInteractable {
    public boolean isFilled();

    public void setFilled(boolean filled);

    public AbstractEntity getEligibleItem();

    public void setEligibleItem(AbstractEntity item);

    /**
     * Executes the command "Fill".
     * 
     * @param obj the object you should fill this object with.
     * @return {@code true} if it's successfully filled,
     *         {@code false} otherwise.
     */
    public boolean fill(AbstractEntity obj);

    public default void lookAt() {
        if (isFilled()) {
            GUIManager.appendOutput("É pieno di: " + getEligibleItem().getName());
        } else {
            GUIManager.appendOutput("È vuoto.");
        }
    }
}
