package di.uniba.map.b.adventure.entities;

import java.util.List;
import java.util.Set;
import di.uniba.map.b.adventure.type.EventType;
import di.uniba.map.b.adventure.type.AbstractRoom;

public class AdvFire extends AbstractEntity {

    private boolean lit = false;

    public AdvFire(int id, String name, String description) {
        super(id, name, description);
    }

    public AdvFire(int id, String name, String description, Set<String> alias) {
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
    public void processReferences(List<AbstractEntity> objects, List<AbstractRoom> rooms) {
        processRoomParent(rooms);
        processEventReferences(objects, rooms);
    }

}
