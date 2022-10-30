package component.entity.object;

import java.util.List;
import com.google.common.collect.Multimap;
import component.entity.AbstractEntity;
import component.entity.interfaces.IPullable;
import component.entity.interfaces.IWearable;
import component.event.EventType;
import component.room.AbstractRoom;

public class PullableObject extends AbstractEntity implements IPullable {

    private boolean pulled = false;

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
    public void processReferences(Multimap<String, AbstractEntity> objects,
            List<AbstractRoom> rooms) {
        processRoomParent(rooms);
        processEventReferences(objects, rooms);
    }

}
