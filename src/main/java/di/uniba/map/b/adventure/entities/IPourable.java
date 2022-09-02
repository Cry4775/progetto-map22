package di.uniba.map.b.adventure.entities;

public interface IPourable {
    public boolean pourOn(StringBuilder outString, AbstractEntity target);
}
