package di.uniba.map.b.adventure.type;

public interface IPushable {
    public boolean isPushed();

    public void setPushed(boolean value);

    public boolean push(StringBuilder outString);
}
