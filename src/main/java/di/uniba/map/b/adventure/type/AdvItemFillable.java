package di.uniba.map.b.adventure.type;

import java.util.Set;

public class AdvItemFillable extends AdvItem implements IFillable {

    private boolean filled;

    private AdvItem eligibleItem;
    private Integer eligibleItemId;

    public AdvItemFillable(int id, String name, String description, Set<String> alias) {
        super(id, name, description, alias);
    }

    public AdvItemFillable(int id) {
        super(id);
    }

    public AdvItemFillable(int id, String name) {
        super(id, name);
    }

    public AdvItemFillable(int id, String name, String description) {
        super(id, name, description);
    }

    public boolean isFilled() {
        return filled;
    }

    public void setFilled(boolean value) {
        filled = value;
    }

    public AdvItem getEligibleItem() {
        return eligibleItem;
    }

    public void setEligibleItem(AdvItem eligibleItem) {
        this.eligibleItem = eligibleItem;
    }

    public Integer getEligibleItemId() {
        return eligibleItemId;
    }

    public void setEligibleItemId(Integer eligibleItemId) {
        this.eligibleItemId = eligibleItemId;
    }

    @Override
    public boolean fill(StringBuilder outString) {
        // TODO Auto-generated method stub
        return false;
    }

}
