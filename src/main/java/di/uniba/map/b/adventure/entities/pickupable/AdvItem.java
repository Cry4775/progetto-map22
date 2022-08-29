package di.uniba.map.b.adventure.entities.pickupable;

import java.util.List;
import java.util.Set;
import di.uniba.map.b.adventure.entities.AbstractEntity;
import di.uniba.map.b.adventure.entities.IPickupable;
import di.uniba.map.b.adventure.entities.container.AbstractContainer;
import di.uniba.map.b.adventure.type.EventType;

public class AdvItem extends AbstractEntity implements IPickupable {

    private String inventoryDescription;

    private boolean pickedUp = false; // TODO forse c'é un modo migliore

    private boolean activable;

    private boolean active;

    public AdvItem(int id, String name, String description, Set<String> alias) {
        super(id, name, description, alias);
    }

    public AdvItem(int id) {
        super(id);
    }

    public AdvItem(int id, String name) {
        super(id, name);
    }

    public AdvItem(int id, String name, String description) {
        super(id, name, description);
    }

    public boolean isActivable() {
        return activable;
    }

    public void setActivable(boolean activable) {
        this.activable = activable;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getInventoryDescription() {
        return inventoryDescription;
    }

    public void setInventoryDescription(String inventoryDescription) {
        this.inventoryDescription = inventoryDescription;
    }

    public boolean isPickedUp() {
        return pickedUp;
    }

    public void setPickedUp(boolean value) {
        pickedUp = value;
    }

    @Override
    public boolean pickup(StringBuilder outString, List<AbstractEntity> inventory,
            List<AbstractEntity> roomObjects) {
        if (!pickedUp) {
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
        } else {
            outString.append("É giá nel tuo inventario.");
            return false;
        }
    }
}
