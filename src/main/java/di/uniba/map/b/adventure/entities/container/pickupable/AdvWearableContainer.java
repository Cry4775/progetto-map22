package di.uniba.map.b.adventure.entities.container.pickupable;

import java.util.List;
import java.util.Set;
import di.uniba.map.b.adventure.entities.AbstractEntity;
import di.uniba.map.b.adventure.entities.IWearable;
import di.uniba.map.b.adventure.entities.container.AbstractContainer;
import di.uniba.map.b.adventure.type.EventType;

public class AdvWearableContainer extends AbstractContainer implements IWearable {

    private boolean worn = false;
    private boolean pickedUp = false;
    private int maxSlots;

    public AdvWearableContainer(int id) {
        super(id);
    }

    public AdvWearableContainer(int id, String name) {
        super(id, name);
    }

    public AdvWearableContainer(int id, String name, String description) {
        super(id, name, description);
    }

    public AdvWearableContainer(int id, String name, String description, Set<String> alias) {
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
    public boolean wear(StringBuilder outString) {
        if (!worn) {
            worn = true;

            outString.append("Hai indossato: " + getName());
            outString.append(processEvent(EventType.WEAR));

            return true;
        } else {
            outString.append("L'hai giá indossato.");
            return false;
        }
    }

    @Override
    public boolean pickup(StringBuilder outString, List<AbstractEntity> inventory,
            List<AbstractEntity> roomObjects) {
        pickedUp = true;
        inventory.add(this);

        // Check if it's an obj inside something and remove it from its list
        if (getParent() != null) {
            AbstractContainer parentContainer = (AbstractContainer) getParent();
            parentContainer.getList().remove(this);
            setParent(null);
        } else {
            roomObjects.remove(this);
        }

        outString.append("Hai raccolto: " + getName());
        outString.append(processEvent(EventType.PICK_UP));

        return true;
    }

    @Override
    public boolean insert(StringBuilder outString, AbstractEntity obj,
            List<AbstractEntity> inventory) {
        if (getList().size() < maxSlots) {
            obj.setParent(this);
            inventory.remove(obj);

            this.add(obj);

            outString.append("Hai lasciato: " + obj.getName());
            outString.append(processEvent(EventType.INSERT));

            return true;
        } else {
            outString.append("Non ci entra piú nulla. Libera spazio o tienilo nell'inventario!");
            return false;
        }
    }

}
