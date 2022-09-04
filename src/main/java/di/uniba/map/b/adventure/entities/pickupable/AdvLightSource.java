package di.uniba.map.b.adventure.entities.pickupable;

import java.util.List;
import java.util.Set;
import di.uniba.map.b.adventure.entities.AbstractEntity;
import di.uniba.map.b.adventure.entities.ILightSource;
import di.uniba.map.b.adventure.type.Room;

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
    public StringBuilder turnOn() {
        StringBuilder outString = new StringBuilder();

        if (!lighted) {
            lighted = true;
            outString.append("Hai acceso: " + getName());

            setActionPerformed(true);
        } else {
            outString.append(getName() + " é giá acceso.");
        }
        return outString;
    }

    @Override
    public StringBuilder turnOff() {
        StringBuilder outString = new StringBuilder();

        if (lighted) {
            lighted = false;
            outString.append("Hai spento: " + getName());

            setActionPerformed(true);
        } else {
            outString.append(getName() + " é giá spento.");
        }
        return outString;
    }

    @Override
    public void processReferences(List<AbstractEntity> objects, List<Room> rooms) {
        super.processReferences(objects, rooms);

        if (requiredItemId != null) {
            objects.stream()
                    .filter(AdvItem.class::isInstance)
                    .filter(reqItem -> reqItem.getId() == requiredItemId)
                    .forEach(reqItem -> setRequiredItem(reqItem));
        }
    }

}
