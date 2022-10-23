package di.uniba.map.b.adventure.component.entity.pickupable;

import java.util.List;
import java.util.Set;
import di.uniba.map.b.adventure.component.entity.AbstractEntity;
import di.uniba.map.b.adventure.component.entity.container.AbstractContainer;
import di.uniba.map.b.adventure.component.entity.interfaces.IFillable;
import di.uniba.map.b.adventure.component.entity.interfaces.IFluid;
import di.uniba.map.b.adventure.component.event.EventType;
import di.uniba.map.b.adventure.component.room.PlayableRoom;

public class FluidItem extends BasicItem implements IFluid {

    public FluidItem(int id, String name, String description) {
        super(id, name, description);
    }

    public FluidItem(int id, String name, String description, Set<String> alias) {
        super(id, name, description, alias);
    }

    @Override
    public StringBuilder pickup(List<AbstractEntity> inventory) {
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
                        if (getParent() instanceof AbstractContainer) {
                            AbstractContainer parentContainer = (AbstractContainer) getParent();
                            parentContainer.getList().remove(this);
                            setParent(null);
                        } else if (getParent() instanceof PlayableRoom) {
                            PlayableRoom room = (PlayableRoom) getParent();
                            room.getObjects().remove(this);
                        }
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
