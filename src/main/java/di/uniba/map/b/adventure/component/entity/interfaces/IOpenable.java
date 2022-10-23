package di.uniba.map.b.adventure.component.entity.interfaces;

import di.uniba.map.b.adventure.component.entity.AbstractEntity;

public interface IOpenable {
    public boolean isOpen();

    public void setOpen(boolean value);

    public boolean isLocked();

    public void setLocked(boolean value);

    public int getUnlockedWithItemId();

    public StringBuilder open(AbstractEntity key);
}
