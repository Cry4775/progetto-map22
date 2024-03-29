package component.entity.interfaces;

import component.entity.AbstractEntity;
import gui.GUIManager;
import engine.MoveInformations.ActionState;

public interface IOpenable extends IInteractable {
    public boolean isOpen();

    public void setOpen(boolean open);

    public boolean isLocked();

    public void setLocked(boolean locked);

    public String getUnlockedWithItemId();

    /**
     * Executes the action "Open".
     * 
     * @param key the parsed inventory object that should represent the key. Can be {@code null}.
     * @return the action state.
     */
    public ActionState open(AbstractEntity key);

    public default void lookAt() {
        if (isLocked()) {
            GUIManager.appendOutput("È chiuso a chiave.");
        } else if (!isOpen()) {
            GUIManager.appendOutput("È chiuso.");
        } else if (isOpen()) {
            GUIManager.appendOutput("È aperto.");
        }
    }
}
