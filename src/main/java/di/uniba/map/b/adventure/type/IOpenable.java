package di.uniba.map.b.adventure.type;

public interface IOpenable {
    public boolean isOpen();

    public void setOpen(boolean value);

    public boolean open(StringBuilder outString, AdvObject key);
}
