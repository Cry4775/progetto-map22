package component.entity.interfaces;

public interface IPushable extends IInteractable {
    public boolean isPushed();

    public void setPushed(boolean pushed);

    public StringBuilder push();
}
