package di.uniba.map.b.adventure.type;

public interface IPullable {
    public boolean isPulled();

    public void setPulled(boolean value);

    public boolean pull(StringBuilder outString);
}
