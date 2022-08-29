package di.uniba.map.b.adventure.entities.container.pickupable;

import java.util.List;
import java.util.Set;
import di.uniba.map.b.adventure.entities.AdvObject;
import di.uniba.map.b.adventure.entities.IWearable;
import di.uniba.map.b.adventure.entities.container.AbstractContainer;

public class AdvWearableContainer extends AbstractContainer implements IWearable {

    private boolean worn = false;
    private boolean pickedUp = false;

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

    @Override
    public boolean isWorn() {
        return worn;
    }

    @Override
    public void setWorn(boolean value) {
        this.worn = value;
    }

    @Override
    public boolean wear(StringBuilder outString) {
        if (!worn) {
            worn = true;

            outString.append("Hai indossato: " + getName());

            return true;
        } else {
            outString.append("L'hai giá indossato.");
            return false;
        }
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
    public boolean pickup(StringBuilder outString, List<AdvObject> inventory,
            List<AdvObject> roomObjects) {
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
        // outString.append(handleObjEvent(item.getEvent(EventType.PICK_UP)));

        return true;
    }

}
