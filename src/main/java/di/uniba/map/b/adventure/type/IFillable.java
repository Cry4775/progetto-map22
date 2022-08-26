package di.uniba.map.b.adventure.type;

public interface IFillable {
    public boolean isFilled();

    public void setFilled(boolean value);

    public boolean fill(StringBuilder outString);
}
