package di.uniba.map.b.adventure.entities.pickupable;

import java.util.Set;
import di.uniba.map.b.adventure.entities.AbstractEntity;
import di.uniba.map.b.adventure.entities.ILightSource;

public class AdvLightSource extends AdvItem implements ILightSource {

    private boolean lighted = false;

    private AdvItem requiredItem;
    private Integer requiredItemId;

    public AdvLightSource(int id, String name, String description, Set<String> alias) {
        super(id, name, description, alias);
    }

    public AdvLightSource(int id) {
        super(id);
    }

    public AdvLightSource(int id, String name) {
        super(id, name);
    }

    public AdvLightSource(int id, String name, String description) {
        super(id, name, description);
    }

    @Override
    public boolean isOn() {
        return lighted;
    }

    @Override
    public void setOn(boolean value) {
        lighted = value;
    }

    public AdvItem getRequiredItem() {
        return requiredItem;
    }

    public void setRequiredItem(AbstractEntity requiredItem) {
        this.requiredItem = (AdvItem) requiredItem;
    }

    public Integer getRequiredItemId() {
        return requiredItemId;
    }

    public void setRequiredItemId(Integer requiredItemId) {
        this.requiredItemId = requiredItemId;
    }

    @Override
    public boolean turnOn(StringBuilder outString) {
        if (!lighted) {
            lighted = true;
            outString.append("Hai acceso: " + getName());
            return true;
        } else {
            outString.append(getName() + " é giá acceso.");
            return false;
        }
    }

    @Override
    public boolean turnOff(StringBuilder outString) {
        if (lighted) {
            lighted = false;
            outString.append("Hai spento: " + getName());
            return true;
        } else {
            outString.append(getName() + " é giá spento.");
            return false;
        }
    }

}
