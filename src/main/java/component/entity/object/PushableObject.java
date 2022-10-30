package component.entity.object;

import java.util.List;
import com.google.common.collect.Multimap;
import component.entity.AbstractEntity;
import component.entity.interfaces.IPushable;
import component.entity.interfaces.IWearable;
import component.event.EventType;
import component.room.AbstractRoom;

public class PushableObject extends AbstractEntity implements IPushable {

    private boolean pushed = false;

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

    @Override
    public void processReferences(Multimap<String, AbstractEntity> objects,
            List<AbstractRoom> rooms) {
        processRoomParent(rooms);
        processEventReferences(objects, rooms);
    }

}
