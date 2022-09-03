package di.uniba.map.b.adventure.entities;

public interface IPushable {
    public boolean isPushed();

    public void setPushed(boolean value);

    public StringBuilder push();
}
