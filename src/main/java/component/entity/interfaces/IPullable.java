package component.entity.interfaces;

public interface IPullable {
    public boolean isPulled();

    public void setPulled(boolean pulled);

    public StringBuilder pull();
}
