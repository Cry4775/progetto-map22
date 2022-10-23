package di.uniba.map.b.adventure.component.entity.object;

import java.util.List;
import java.util.Set;
import com.google.common.collect.Multimap;
import di.uniba.map.b.adventure.type.EventType;
import di.uniba.map.b.adventure.component.entity.AbstractEntity;
import di.uniba.map.b.adventure.component.entity.interfaces.IFluid;
import di.uniba.map.b.adventure.component.entity.interfaces.IWearable;
import di.uniba.map.b.adventure.component.room.AbstractRoom;

public class FireObject extends AbstractEntity {

    private boolean lit = false;

    public FireObject(int id, String name, String description) {
        super(id, name, description);
    }

    public FireObject(int id, String name, String description, Set<String> alias) {
        super(id, name, description, alias);
    }

    public boolean isLit() {
        return lit;
    }

    public void setLit(boolean lit) {
        this.lit = lit;
    }

    public StringBuilder extinguish() {
        StringBuilder outString = new StringBuilder();

        if (lit) {

            if (getRequiredWearedItemsToInteract() != null) {
                for (IWearable wearable : getRequiredWearedItemsToInteract()) {
                    if (!wearable.isWorn()) {
                        outString.append(getFailedInteractionMessage()); // TODO check per null e
                                                                         // default message
                        return outString;
                    }
                }
            }

            lit = false;

            outString.append(processEvent(EventType.EXTINGUISH));

            setActionPerformed(true);
        } else {
            outString.append("Non ci sono piú fiamme.");
        }

        return outString;
    }

    public StringBuilder extinguish(IFluid liquid) {
        StringBuilder outString = new StringBuilder();

        if (lit) {

            if (getRequiredWearedItemsToInteract() != null) {
                for (IWearable wearable : getRequiredWearedItemsToInteract()) {
                    if (!wearable.isWorn()) {
                        outString.append(getFailedInteractionMessage()); // TODO check per null e
                                                                         // default message
                        return outString;
                    }
                }
            }

            lit = false;
            liquid.delete();

            outString.append(processEvent(EventType.EXTINGUISH));
            setActionPerformed(true);
        } else {
            outString.append("Non ci sono piú fiamme.");
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
