package component.entity.pickupable;

import java.util.List;
import java.util.Set;
import com.google.common.collect.Multimap;
import component.entity.AbstractEntity;
import component.entity.interfaces.IFillable;
import component.room.AbstractRoom;

public class FillableItem extends BasicItem implements IFillable {

    private boolean filled;

    private AbstractEntity eligibleItem;
    private Integer eligibleItemId;

    public FillableItem(int id, String name, String description) {
        super(id, name, description);
    }

    public FillableItem(int id, String name, String description, Set<String> alias) {
        super(id, name, description, alias);
    }

    public boolean isFilled() {
        return filled;
    }

    public void setFilled(boolean value) {
        filled = value;
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

    public void setEligibleItemId(Integer eligibleItemId) {
        this.eligibleItemId = eligibleItemId;
    }

    @Override
    public boolean fill(AbstractEntity obj) {
        if (eligibleItem.equals(obj)) {
            filled = true;
            // TODO distinzione tra riempi e prendi
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void processReferences(Multimap<Integer, AbstractEntity> objects,
            List<AbstractRoom> rooms) {
        super.processReferences(objects, rooms);

        if (eligibleItemId != null) {
            if (!objects.containsKey(eligibleItemId)) {
                throw new Error(
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
