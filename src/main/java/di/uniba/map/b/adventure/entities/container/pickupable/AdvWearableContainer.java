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
    public StringBuilder pickup(List<AbstractEntity> inventory,
            List<AbstractEntity> roomObjects) {
        StringBuilder outString = new StringBuilder();

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

        setActionPerformed(true);
        return outString;
    }

    @Override
    public StringBuilder insert(AbstractEntity obj, List<AbstractEntity> inventory) {
        StringBuilder outString = new StringBuilder();

        if (getList().size() < maxSlots) {
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
