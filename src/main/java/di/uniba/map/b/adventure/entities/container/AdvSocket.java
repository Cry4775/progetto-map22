package di.uniba.map.b.adventure.entities.container;

import java.util.List;
import java.util.Set;
import di.uniba.map.b.adventure.entities.AbstractEntity;
import di.uniba.map.b.adventure.entities.IWearable;
import di.uniba.map.b.adventure.entities.pickupable.AdvItem;
import di.uniba.map.b.adventure.type.EventType;
import di.uniba.map.b.adventure.type.AbstractRoom;

public class AdvSocket extends AbstractContainer {

    private boolean itemInside = false;

    private AbstractEntity eligibleItem;
    private Integer eligibleItemId;

    public AdvSocket(int id, String name, String description) {
        super(id, name, description);
    }

    public AdvSocket(int id, String name, String description, Set<String> alias) {
        super(id, name, description, alias);
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

    @Override
    public StringBuilder insert(AbstractEntity obj, List<AbstractEntity> inventory) {
        StringBuilder outString = new StringBuilder();

        if (!itemInside) {
            if (eligibleItem.equals(obj)) {
                if (obj instanceof IWearable) {
                    IWearable wearable = (IWearable) obj;

                    if (wearable.isWorn()) {
                        outString.append("Devi prima toglierlo di dosso.");
                        return outString;
                    }
                }

                if (obj.getParent() instanceof AbstractContainer) {
                    AbstractContainer container = (AbstractContainer) obj.getParent();

                    container.remove(obj);
                }

                itemInside = true;
                obj.setParent(this);
                inventory.remove(obj);

                this.add(obj);

                outString.append("Hai inserito: " + obj.getName());
                outString.append(processEvent(EventType.INSERT));
            } else {
                outString.append("Non puoi inserirci questo oggetto.");
            }
        } else {
            outString.append("Non puoi inserirci altri oggetti.");
        }
        return outString;
    }

    @Override
    public void processReferences(List<AbstractEntity> objects, List<AbstractRoom> rooms) {
        super.processReferences(objects, rooms);

        if (eligibleItemId != null) {
            objects.stream()
                    .filter(AdvItem.class::isInstance)
                    .filter(reqItem -> reqItem.getId() == eligibleItemId)
                    .forEach(reqItem -> setEligibleItem(reqItem));
        }
    }

}
