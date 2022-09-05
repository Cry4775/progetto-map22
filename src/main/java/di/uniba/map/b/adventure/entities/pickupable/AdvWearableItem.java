package di.uniba.map.b.adventure.entities.pickupable;

import java.util.Set;
import di.uniba.map.b.adventure.entities.IWearable;
import di.uniba.map.b.adventure.type.EventType;

public class AdvWearableItem extends AdvItem implements IWearable {

    private boolean worn = false;

    public AdvWearableItem(int id) {
        super(id);
    }

    public AdvWearableItem(int id, String name) {
        super(id, name);
    }

    public AdvWearableItem(int id, String name, String description) {
        super(id, name, description);
    }

    public AdvWearableItem(int id, String name, String description, Set<String> alias) {
        super(id, name, description, alias);
    }

    @Override
    public boolean isWorn() {
        return worn;
    }

    @Override
    public void setWorn(boolean value) {
        this.worn = value;
    }

    @Override
    public StringBuilder wear() {
        StringBuilder outString = new StringBuilder();

        if (!worn) {
            worn = true;

            outString.append("Hai indossato: " + getName());
            outString.append(processEvent(EventType.WEAR));

            setActionPerformed(true);
        } else {
            outString.append("L'hai giá indossato.");
        }
        return outString;
    }

    @Override
    public StringBuilder unwear() {
        StringBuilder outString = new StringBuilder();

        if (worn) {
            worn = false;

            outString.append("Hai tolto: " + getName());
            outString.append(processEvent(EventType.WEAR));

            setActionPerformed(true);
        } else {
            outString.append("Non ce l'hai addosso.");
        }
        return outString;
    }
}
