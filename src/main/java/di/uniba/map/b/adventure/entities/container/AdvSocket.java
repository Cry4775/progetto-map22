package di.uniba.map.b.adventure.entities.container;

import java.util.List;
import java.util.Set;
import di.uniba.map.b.adventure.entities.AbstractEntity;
import di.uniba.map.b.adventure.type.EventType;

public class AdvSocket extends AbstractContainer {

    private boolean itemInside = false;

    private AbstractEntity eligibleItem;
    private Integer eligibleItemId;

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

    public AbstractEntity getEligibleItem() {
        return eligibleItem;
    }

    public void setEligibleItem(AbstractEntity eligibleItem) {
        this.eligibleItem = eligibleItem;
    }

    public Integer getEligibleItemId() {
        return eligibleItemId;
    }

    public void setEligibleItemId(int eligibleItemId) {
        this.eligibleItemId = eligibleItemId;
    }

    public boolean insert(StringBuilder outString, AbstractEntity obj,
            List<AbstractEntity> inventory) {
        if (!itemInside) {
            if (eligibleItem.equals(obj)) {
                itemInside = true;
                obj.setParent(this);
                inventory.remove(obj);

                this.add(obj);

                outString.append("Hai inserito: " + obj.getName());
                outString.append(processEvent(EventType.INSERT));

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
