package component.entity.interfaces;

import component.entity.AbstractEntity;

public interface IOpenable extends IInteractable {
    public boolean isOpen();

    public void setOpen(boolean open);

    public boolean isLocked();

    public void setLocked(boolean locked);

    public String getUnlockedWithItemId();

    public StringBuilder open(AbstractEntity key);
}
