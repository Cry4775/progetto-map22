package di.uniba.map.b.adventure.entities;

public interface IPullable {
    public boolean isPulled();

    public void setPulled(boolean value);

    public boolean pull(StringBuilder outString);
}
