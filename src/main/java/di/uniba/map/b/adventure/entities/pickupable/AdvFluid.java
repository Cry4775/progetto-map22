package di.uniba.map.b.adventure.entities.pickupable;

import java.util.List;
import java.util.Set;
import di.uniba.map.b.adventure.entities.AbstractEntity;
import di.uniba.map.b.adventure.entities.IFillable;
import di.uniba.map.b.adventure.entities.IFluid;
import di.uniba.map.b.adventure.type.EventType;

public class AdvFluid extends AdvItem implements IFluid {

    public AdvFluid(int id, String name, String description, Set<String> alias) {
        super(id, name, description, alias);
    }

    public AdvFluid(int id) {
        super(id);
    }

    public AdvFluid(int id, String name) {
        super(id, name);
    }

    public AdvFluid(int id, String name, String description) {
        super(id, name, description);
    }

    @Override
    public boolean pickup(StringBuilder outString, List<AbstractEntity> inventory,
            List<AbstractEntity> roomObjects) {
        if (isPickedUp()) {
            outString.append("Non puoi riprenderti il fluido dal contenitore.");
            return false;
        }

        boolean canProceed = false;

        for (AbstractEntity invObject : inventory) {
            if (invObject instanceof IFillable) {
                IFillable invFillable = (IFillable) invObject;

                canProceed = invFillable.fill(this);

                if (canProceed) {
                    outString.append("Hai riempito: " + invObject.getName());
                    outString.append(processEvent(EventType.PICK_UP));
                    return true;
                }
            }
        }

        if (!canProceed) {
            outString.append("Non puoi prenderlo senza lo strumento adatto.");
        }

        return false;
    }

}
