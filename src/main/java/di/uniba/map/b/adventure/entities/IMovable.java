package di.uniba.map.b.adventure.entities;

public interface IMovable {
    public boolean isMoved();

    public void setMoved(boolean value);

    public StringBuilder move();
}
