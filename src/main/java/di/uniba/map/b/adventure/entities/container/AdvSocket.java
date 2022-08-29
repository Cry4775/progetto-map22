package di.uniba.map.b.adventure.entities.container;

import java.util.List;
import java.util.Set;
import di.uniba.map.b.adventure.entities.AdvObject;
import di.uniba.map.b.adventure.entities.pickupable.AdvItem;

public class AdvSocket extends AbstractContainer {

    private boolean itemInside = false;

    private AdvItem eligibleItem;
    private int eligibleItemId;

    public AdvSocket(int id, String name, String description, Set<String> alias) {
        super(id, name, description, alias);
    }

    public AdvSocket(int id) {
        super(id);
    }

    public AdvSocket(int id, String name) {
        super(id, name);
    }

    public AdvSocket(int id, String name, String description) {
        super(id, name, description);
    }

    public boolean isItemInside() {
        return itemInside;
    }

    public void setItemInside(boolean itemInside) {
        this.itemInside = itemInside;
    }

    public AdvItem getEligibleItem() {
        return eligibleItem;
    }

    public void setEligibleItem(AdvItem eligibleItem) {
        this.eligibleItem = eligibleItem;
    }

    public int getEligibleItemId() {
        return eligibleItemId;
    }

    public void setEligibleItemId(int eligibleItemId) {
        this.eligibleItemId = eligibleItemId;
    }

    public boolean insert(StringBuilder outString, AdvObject obj, List<AdvObject> inventory) {
        if (!itemInside) {
            if (eligibleItem.equals(obj)) {
                itemInside = true;
                obj.setParent(this);
                inventory.remove(obj);

                this.add(obj);

                outString.append("Hai inserito: " + obj.getName());
                // outString.append(handleObjEvent(obj.getEvent(EventType.INSERT)));

                return true;
            } else {
                outString.append("Non puoi inserirci questo oggetto.");
            }
        } else {
            outString.append("Non puoi inserirci altri oggetti.");
        }
        return false;
    }

}
