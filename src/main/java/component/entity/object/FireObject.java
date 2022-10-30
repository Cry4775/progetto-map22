package component.entity.object;

import java.util.List;
import com.google.common.collect.Multimap;
import component.entity.AbstractEntity;
import component.entity.interfaces.IFluid;
import component.entity.interfaces.IWearable;
import component.event.EventType;
import component.room.AbstractRoom;

public class FireObject extends AbstractEntity {

    private boolean lit = false;

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
    public void processReferences(Multimap<String, AbstractEntity> objects,
            List<AbstractRoom> rooms) {
        processRoomParent(rooms);
        processEventReferences(objects, rooms);
    }

}
