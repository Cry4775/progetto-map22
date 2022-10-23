package di.uniba.map.b.adventure.entities.interfaces;

public interface IPushable {
    public boolean isPushed();

    public void setPushed(boolean value);

    public StringBuilder push();
}
