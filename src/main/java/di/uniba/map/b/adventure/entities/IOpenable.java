package di.uniba.map.b.adventure.entities;

public interface IOpenable {
    public boolean isOpen();

    public void setOpen(boolean value);

    public boolean isLocked();

    public void setLocked(boolean value);

    public int getUnlockedWithItemId();

    public boolean open(StringBuilder outString, AbstractEntity key);
}
