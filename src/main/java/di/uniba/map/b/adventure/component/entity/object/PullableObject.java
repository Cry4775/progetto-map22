package di.uniba.map.b.adventure.component.entity.object;

import java.util.List;
import java.util.Set;
import com.google.common.collect.Multimap;
import di.uniba.map.b.adventure.type.EventType;
import di.uniba.map.b.adventure.component.entity.AbstractEntity;
import di.uniba.map.b.adventure.component.entity.interfaces.IPullable;
import di.uniba.map.b.adventure.component.entity.interfaces.IWearable;
import di.uniba.map.b.adventure.component.room.AbstractRoom;

public class PullableObject extends AbstractEntity implements IPullable {

    private boolean pulled = false;

    public PullableObject(int id, String name, String description) {
        super(id, name, description);
    }

    public PullableObject(int id, String name, String description, Set<String> alias) {
        super(id, name, description, alias);
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

    @Override
    public void processReferences(Multimap<Integer, AbstractEntity> objects,
            List<AbstractRoom> rooms) {
        processRoomParent(rooms);
        processEventReferences(objects, rooms);
    }

}
