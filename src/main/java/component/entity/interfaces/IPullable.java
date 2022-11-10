package component.entity.interfaces;

public interface IPullable extends IInteractable {
    public boolean isPulled();

    public void setPulled(boolean pulled);

    public StringBuilder pull();
}
