package di.uniba.map.b.adventure.type;

import java.util.List;
import java.util.Set;

public class AdvItem extends AdvObject implements IPickupable {

    private String inventoryDescription;

    private AdvItem parent;

    private boolean pickupableWithFillableItem;

    private boolean activable;

    private boolean picked; // TODO forse eliminare e fare un sistema che controlli l'inv
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

    public AdvItem getParent() {
        return parent;
    }

    public void setParent(AdvItem parent) {
        this.parent = parent;
    }

    public boolean isPicked() {
        return picked;
    }

    public void setPicked(boolean picked) {
        this.picked = picked;
    }

    public boolean isPickupableWithFillableItem() {
        return pickupableWithFillableItem;
    }

    public void setPickupableWithFillableItem(boolean pickupableWithFillableItem) {
        this.pickupableWithFillableItem = pickupableWithFillableItem;
    }

    @Override
    public boolean pickUp(StringBuilder outString, List<AdvObject> inventory) {
        // TODO Auto-generated method stub
        return false;
    }
}
