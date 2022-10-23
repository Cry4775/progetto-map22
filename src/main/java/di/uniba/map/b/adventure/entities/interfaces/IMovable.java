package di.uniba.map.b.adventure.entities.interfaces;

public interface IMovable {
    public boolean isMoved();

    public void setMoved(boolean value);

    public StringBuilder move();
}
