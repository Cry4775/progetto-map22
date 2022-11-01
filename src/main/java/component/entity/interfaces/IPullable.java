package component.entity.interfaces;

public interface IPullable {
    public boolean isPulled();

    public void setPulled(boolean value);

    public StringBuilder pull();
}