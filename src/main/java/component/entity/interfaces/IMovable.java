package component.entity.interfaces;

public interface IMovable extends IInteractable {
    public boolean isMoved();

    public void setMoved(boolean moved);

    public StringBuilder move();
}
