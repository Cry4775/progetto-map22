package di.uniba.map.b.adventure.component.entity.object;

import java.util.List;
import java.util.Set;
import com.google.common.collect.Multimap;
import di.uniba.map.b.adventure.component.entity.AbstractEntity;
import di.uniba.map.b.adventure.component.entity.interfaces.IMovable;
import di.uniba.map.b.adventure.component.entity.interfaces.IWearable;
import di.uniba.map.b.adventure.component.event.EventType;
import di.uniba.map.b.adventure.component.room.AbstractRoom;

public class MovableObject extends AbstractEntity implements IMovable {

    private boolean moved = false;

    public MovableObject(int id, String name, String description) {
        super(id, name, description);
    }

    public MovableObject(int id, String name, String description, Set<String> alias) {
        super(id, name, description, alias);
    }

    @Override
    public boolean isMoved() {
        return moved;
    }

    @Override
    public void setMoved(boolean value) {
        moved = value;
    }

    @Override
    public StringBuilder move() {
        StringBuilder outString = new StringBuilder();

        if (!moved) {

            if (getRequiredWearedItemsToInteract() != null) {
                for (IWearable wearable : getRequiredWearedItemsToInteract()) {
                    if (!wearable.isWorn()) {
                        outString.append(getFailedInteractionMessage());
                        return outString;
                    }
                }
            }

            moved = true;
            outString.append("Hai spostato: " + getName());
            outString.append(processEvent(EventType.MOVE));

            setActionPerformed(true);
        } else {
            outString.append("È stato già spostato.");
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
