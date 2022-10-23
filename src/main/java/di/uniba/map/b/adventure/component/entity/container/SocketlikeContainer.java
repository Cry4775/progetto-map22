package di.uniba.map.b.adventure.component.entity.container;

import java.util.List;
import java.util.Set;
import com.google.common.collect.Multimap;
import di.uniba.map.b.adventure.component.entity.AbstractEntity;
import di.uniba.map.b.adventure.component.entity.interfaces.IWearable;
import di.uniba.map.b.adventure.component.room.AbstractRoom;
import di.uniba.map.b.adventure.type.EventType;

public class SocketlikeContainer extends AbstractContainer {

    private boolean itemInside = false;

    private AbstractEntity eligibleItem;
    private Integer eligibleItemId;

    public SocketlikeContainer(int id, String name, String description) {
        super(id, name, description);
    }

    public SocketlikeContainer(int id, String name, String description, Set<String> alias) {
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
    public void processReferences(Multimap<Integer, AbstractEntity> objects,
            List<AbstractRoom> rooms) {
        super.processReferences(objects, rooms);

        if (eligibleItemId != null) {
            if (!objects.containsKey(eligibleItemId)) {
                throw new RuntimeException(
                        "Couldn't find the requested \"eligibleItem\" ID on " + getName()
                                + " (" + getId()
                                + "). Check the JSON file for correct object IDs.");
            }

            for (AbstractEntity reqItem : objects.get(eligibleItemId)) {
                eligibleItem = reqItem;
            }
        }
    }

}
