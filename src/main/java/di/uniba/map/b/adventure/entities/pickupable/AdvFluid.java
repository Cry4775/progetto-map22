package di.uniba.map.b.adventure.entities.pickupable;

import java.util.List;
import java.util.Set;
import di.uniba.map.b.adventure.entities.AbstractEntity;
import di.uniba.map.b.adventure.entities.IFillable;
import di.uniba.map.b.adventure.entities.IFluid;
import di.uniba.map.b.adventure.entities.container.AbstractContainer;
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
    public StringBuilder pickup(List<AbstractEntity> inventory, List<AbstractEntity> roomObjects) {
        StringBuilder outString = new StringBuilder();

        if (isPickedUp()) {
            outString.append("É giá nel tuo inventario.");
            return outString;
        }

        boolean canProceed = false;

        for (AbstractEntity invObject : inventory) {
            if (invObject instanceof IFillable) {
                IFillable invFillable = (IFillable) invObject;

                canProceed = invFillable.fill(this);

                if (canProceed) {
                    outString.append("Hai riempito: " + invObject.getName());
                    outString.append(processEvent(EventType.PICK_UP));

                    setActionPerformed(true);

                    if (getParent() != null) {
                        AbstractContainer parentContainer = (AbstractContainer) getParent();
                        parentContainer.getList().remove(this);
                        setParent(invObject);
                    } else {
                        roomObjects.remove(this);
                    }

                    setPickedUp(true);
                    break;
                }
            }
        }

        if (!canProceed) {
            outString.append("Non puoi prenderlo senza lo strumento adatto.");
        }

        return outString;
    }

    @Override
    public void delete() {
        if (getParent() instanceof IFillable) {
            IFillable container = (IFillable) getParent();

            container.setFilled(false);
            setParent(null);
        }
    }

}
