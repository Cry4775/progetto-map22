package component.entity.interfaces;

import component.entity.AbstractEntity;
import engine.OutputManager;

public interface IOpenable extends IInteractable {
    public boolean isOpen();

    public void setOpen(boolean open);

    public boolean isLocked();

    public void setLocked(boolean locked);

    public String getUnlockedWithItemId();

    public StringBuilder open(AbstractEntity key);

    public default void sendLookMessage() {
        if (isLocked()) {
            OutputManager.append("È chiuso a chiave.");
        } else if (!isOpen()) {
            OutputManager.append("È chiuso.");
        } else if (isOpen()) {
            OutputManager.append("È aperto.");
        }
    }
}
