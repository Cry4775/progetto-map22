package di.uniba.map.b.adventure.type;

import java.util.Set;

public class AdvItemWearable extends AdvItem implements IWearable {

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

    @Override
    public boolean isWorn() {
        return worn;
    }

    @Override
    public void setWorn(boolean value) {
        this.worn = value;
    }

    @Override
    public boolean wear(StringBuilder outString) {
        if (!worn) {
            worn = true;

            outString.append("Hai indossato: " + getName());

            return true;
        } else {
            outString.append("L'hai giá indossato.");
            return false;
        }
    }

}
