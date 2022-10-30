package component.entity.pickupable;

import java.util.List;
import com.google.common.collect.Multimap;
import component.entity.AbstractEntity;
import component.entity.interfaces.ILightSource;
import component.room.AbstractRoom;

public class LightSourceItem extends BasicItem implements ILightSource {

    private boolean lighted = false;

    private BasicItem requiredItem;
    private String requiredItemId;

    @Override
    public boolean isOn() {
        return lighted;
    }

    @Override
    public void setOn(boolean value) {
        lighted = value;
    }

    public BasicItem getRequiredItem() {
        return requiredItem;
    }

    public void setRequiredItem(AbstractEntity requiredItem) {
        this.requiredItem = (BasicItem) requiredItem;
    }

    public String getRequiredItemId() {
        return requiredItemId;
    }

    public void setRequiredItemId(String requiredItemId) {
        this.requiredItemId = requiredItemId;
    }

    @Override
    public StringBuilder turnOn() {
        StringBuilder outString = new StringBuilder();

        if (!lighted) {

            if (requiredItem != null && requiredItem.isPickedUp()) {

                lighted = true;
                outString.append("Hai acceso: " + getName());

                setActionPerformed(true);
            } else if (requiredItem != null && !requiredItem.isPickedUp()) {
                outString.append("Non puoi farlo senza lo strumento adatto.");
            } else {
                lighted = true;
                outString.append("Hai acceso: " + getName());

                setActionPerformed(true);
            }
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
    public void processReferences(Multimap<String, AbstractEntity> objects,
            List<AbstractRoom> rooms) {
        super.processReferences(objects, rooms);

        if (requiredItemId != null) {
            if (!objects.containsKey(requiredItemId)) {
                throw new Error(
                        "Couldn't find the requested \"requiredItem\" ID on " + getName()
                                + " (" + getId()
                                + "). Check the JSON file for correct object IDs.");
            }

            for (AbstractEntity reqItem : objects.get(requiredItemId)) {
                requiredItem = (BasicItem) reqItem;
            }
        }
    }

}
