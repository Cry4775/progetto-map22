package di.uniba.map.b.adventure.type;

public interface IMovable {
    public boolean isMoved();

    public void setMoved(boolean value);

    public boolean move(StringBuilder outString);
}
