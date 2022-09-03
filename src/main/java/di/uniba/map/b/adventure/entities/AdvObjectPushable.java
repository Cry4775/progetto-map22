package di.uniba.map.b.adventure.entities;

import java.util.Set;
import di.uniba.map.b.adventure.games.Status;
import di.uniba.map.b.adventure.type.EventType;

// TODO genera gli equals per tutti gli object

public class AdvObjectPushable extends AbstractEntity implements IPushable {

    private boolean pushed = false;

    public AdvObjectPushable(int id, String name, String description, Set<String> alias) {
        super(id, name, description, alias);
    }

    public AdvObjectPushable(int id) {
        super(id);
    }

    public AdvObjectPushable(int id, String name) {
        super(id, name);
    }

    public AdvObjectPushable(int id, String name, String description) {
        super(id, name, description);
    }

    @Override
    public boolean isPushed() {
        return pushed;
    }

    @Override
    public void setPushed(boolean value) {
        pushed = value;
    }

    @Override
    public StringBuilder push() {
        StringBuilder outString = new StringBuilder();

        if (!pushed) {
            if (getRequiredWearedItemsToInteract() != null) {
                for (IWearable wearable : getRequiredWearedItemsToInteract()) {
                    if (!wearable.isWorn()) {
                        outString.append(getFailedInteractionMessage());
                        return outString;
                    }
                }
            }

            pushed = true;

            outString.append("Hai premuto: " + getName());
            outString.append(processEvent(EventType.PUSH));

            setActionPerformed(true);
        } else {
            outString.append("È stato già premuto.");
        }
        return outString;
    }

}
