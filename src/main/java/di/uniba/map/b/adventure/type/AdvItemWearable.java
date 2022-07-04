package di.uniba.map.b.adventure.type;

import java.util.Set;

public class AdvItemWearable extends AdvItem {

    private boolean worn = false;

    public AdvItemWearable(int id) {
        super(id);
    }

    public AdvItemWearable(int id, String name) {
        super(id, name);
    }

    public AdvItemWearable(int id, String name, String description) {
        super(id, name, description);
    }

    public AdvItemWearable(int id, String name, String description, Set<String> alias) {
        super(id, name, description, alias);
    }

    public boolean isWorn() {
        return worn;
    }

    public void setWorn(boolean worn) {
        this.worn = worn;
    }

}
