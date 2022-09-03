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
    public StringBuilder pull() {
        StringBuilder outString = new StringBuilder();

        if (!pulled) {

            if (getRequiredWearedItemsToInteract() != null) {
                for (IWearable wearable : getRequiredWearedItemsToInteract()) {
                    if (!wearable.isWorn()) {
                        outString.append(getFailedInteractionMessage());
                        return outString;
                    }
                }
            }

            pulled = true;

            outString.append("Hai tirato: " + getName());
            outString.append(processEvent(EventType.PULL));

            setActionPerformed(true);
        } else {
            outString.append("È stato già tirato.");
        }
        return outString;
    }

}
