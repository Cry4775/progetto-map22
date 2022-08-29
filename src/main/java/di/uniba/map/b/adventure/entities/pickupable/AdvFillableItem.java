package di.uniba.map.b.adventure.entities.pickupable;

import java.util.Set;
import di.uniba.map.b.adventure.entities.AbstractEntity;
import di.uniba.map.b.adventure.entities.IFillable;

public class AdvFillableItem extends AdvItem implements IFillable {

    private boolean filled;

    private AbstractEntity eligibleItem;
    private Integer eligibleItemId;

    public AdvFillableItem(int id, String name, String description, Set<String> alias) {
        super(id, name, description, alias);
    }

    public AdvFillableItem(int id) {
        super(id);
    }

    public AdvFillableItem(int id, String name) {
        super(id, name);
    }

    public AdvFillableItem(int id, String name, String description) {
        super(id, name, description);
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

}
