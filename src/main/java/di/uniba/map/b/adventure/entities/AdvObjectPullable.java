package di.uniba.map.b.adventure.entities;

import java.util.Set;
import di.uniba.map.b.adventure.type.EventType;

public class AdvObjectPullable extends AbstractEntity implements IPullable {

    private boolean pulled = false;

    public AdvObjectPullable(int id, String name, String description, Set<String> alias) {
        super(id, name, description, alias);
    }

    public AdvObjectPullable(int id) {
        super(id);
    }

    public AdvObjectPullable(int id, String name) {
        super(id, name);
    }

    public AdvObjectPullable(int id, String name, String description) {
        super(id, name, description);
    }

    @Override
    public boolean isPulled() {
        return pulled;
    }

    @Override
    public void setPulled(boolean value) {
        pulled = value;
    }

    @Override
    public boolean pull(StringBuilder outString) {
        if (!pulled) {
            pulled = true;

            outString.append("Hai tirato: " + getName());
            outString.append(processEvent(EventType.PULL));

            return true;
        } else {
            outString.append("È stato già tirato.");
            return false;
        }
    }

}
