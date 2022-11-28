package engine.command;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Command {

    public enum Type {
        END, INVENTORY, NORTH, NORTH_EAST, NORTH_WEST,
        SOUTH, SOUTH_EAST, SOUTH_WEST, EAST, WEST, UP, DOWN,
        OPEN, CLOSE, PUSH, PULL, WALK_TO, PICK_UP, TALK_TO,
        GIVE, USE, LOOK_AT, TURN_ON, TURN_OFF, MOVE, INSERT,
        WEAR, UNWEAR, POUR, READ, SAVE
    }

    private final Type type;

    private final String name;

    private Set<String> alias;

    public Command(Type type, String name) {
        this.type = type;
        this.name = name;
        alias = new HashSet<>();
    }

    public String getName() {
        return name;
    }

    public Set<String> getAlias() {
        return alias;
    }

    public void setAlias(Set<String> alias) {
        this.alias = alias;
    }

    public void setAlias(String[] alias) {
        this.alias = new HashSet<>(Arrays.asList(alias));
    }

    public void addAlias(String alias) {
        this.alias.add(alias);
    }

    public Type getType() {
        return type;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.type);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Command other = (Command) obj;
        if (this.type != other.type) {
            return false;
        }
        return true;
    }

}
