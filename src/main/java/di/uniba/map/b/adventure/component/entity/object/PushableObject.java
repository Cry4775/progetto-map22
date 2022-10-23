package di.uniba.map.b.adventure.component.entity.object;

import java.util.List;
import java.util.Set;
import com.google.common.collect.Multimap;
import di.uniba.map.b.adventure.type.EventType;
import di.uniba.map.b.adventure.component.entity.AbstractEntity;
import di.uniba.map.b.adventure.component.entity.interfaces.IPushable;
import di.uniba.map.b.adventure.component.entity.interfaces.IWearable;
import di.uniba.map.b.adventure.component.room.AbstractRoom;

public class PushableObject extends AbstractEntity implements IPushable {

    private boolean pushed = false;

    public PushableObject(int id, String name, String description) {
        super(id, name, description);
    }

    public PushableObject(int id, String name, String description, Set<String> alias) {
        super(id, name, description, alias);
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

    @Override
    public void processReferences(Multimap<Integer, AbstractEntity> objects,
            List<AbstractRoom> rooms) {
        processRoomParent(rooms);
        processEventReferences(objects, rooms);
    }

}
