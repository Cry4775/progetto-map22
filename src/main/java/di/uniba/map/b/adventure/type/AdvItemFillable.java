package di.uniba.map.b.adventure.type;

import java.util.Set;

public class AdvItemFillable extends AdvItem {
    private boolean filled;

    private AdvItem filledWithItem;
    private Integer filledWithItemId;

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

    public void setFilled(boolean filled) {
        this.filled = filled;
    }

    public AdvItem getFilledWithItem() {
        return filledWithItem;
    }

    public void setFilledWithItem(AdvItem filledWithItem) {
        this.filledWithItem = filledWithItem;
    }

    public Integer getFilledWithItemId() {
        return filledWithItemId;
    }

    public void setFilledWithItemId(Integer filledWithItemId) {
        this.filledWithItemId = filledWithItemId;
    }
}
