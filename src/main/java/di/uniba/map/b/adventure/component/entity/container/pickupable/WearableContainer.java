package di.uniba.map.b.adventure.component.entity.container.pickupable;

import java.util.List;
import java.util.Set;
import di.uniba.map.b.adventure.component.entity.AbstractEntity;
import di.uniba.map.b.adventure.component.entity.container.AbstractContainer;
import di.uniba.map.b.adventure.component.entity.interfaces.IWearable;
import di.uniba.map.b.adventure.component.event.EventType;
import di.uniba.map.b.adventure.component.room.PlayableRoom;

public class WearableContainer extends AbstractContainer implements IWearable {

    private boolean worn = false;
    private boolean pickedUp = false;
    private int maxSlots;

    public WearableContainer(int id, String name, String description) {
        super(id, name, description);
    }

    public WearableContainer(int id, String name, String description, Set<String> alias) {
        super(id, name, description, alias);
    }

    public int getMaxSlots() {
        return maxSlots;
    }

    public void setMaxSlots(int maxSlots) {
        this.maxSlots = maxSlots;
    }

    @Override
    public boolean isWorn() {
        return worn;
    }

    @Override
    public void setWorn(boolean value) {
        this.worn = value;
    }

    @Override
    public boolean isPickedUp() {
        return pickedUp;
    }

    @Override
    public void setPickedUp(boolean value) {
        pickedUp = value;
    }

    @Override
    public StringBuilder wear() {
        StringBuilder outString = new StringBuilder();

        if (!worn) {
            worn = true;

            outString.append("Hai indossato: " + getName());
            outString.append(processEvent(EventType.UNWEAR));

            setActionPerformed(true);
        } else {
            outString.append("L'hai giá indossato.");
        }
        return outString;
    }

    @Override
    public StringBuilder unwear() {
        StringBuilder outString = new StringBuilder();

        if (worn) {
            worn = false;

            outString.append("Hai tolto: " + getName());
            outString.append(processEvent(EventType.UNWEAR));

            setActionPerformed(true);
        } else {
            outString.append("Non ce l'hai addosso.");
        }
        return outString;
    }

    @Override
    public StringBuilder pickup(List<AbstractEntity> inventory) {
        StringBuilder outString = new StringBuilder();

        pickedUp = true;
        inventory.add(this);

        // Check if it's an obj inside something and remove it from its list
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

        outString.append("Hai raccolto: " + getName());
        outString.append(processEvent(EventType.PICK_UP));

        setActionPerformed(true);
        return outString;
    }

    @Override
    public StringBuilder insert(AbstractEntity obj, List<AbstractEntity> inventory) {
        StringBuilder outString = new StringBuilder();

        if (maxSlots == 0) {
            maxSlots = 999;
        }

        if (getList().size() < maxSlots) {
            if (obj instanceof IWearable) {
                IWearable wearable = (IWearable) obj;

                if (wearable.isWorn()) {
                    outString.append("Devi prima toglierlo di dosso.");
                    return outString;
                }
            }

            obj.setParent(this);
            inventory.remove(obj);

            this.add(obj);

            outString.append("Hai lasciato: " + obj.getName());
            outString.append(processEvent(EventType.INSERT));

            setActionPerformed(true);
        } else {
            outString.append("Non ci entra piú nulla. Libera spazio o tienilo nell'inventario!");
        }
        return outString;
    }

}
