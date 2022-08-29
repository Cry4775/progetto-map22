package di.uniba.map.b.adventure.entities;

import java.util.List;

public interface IPickupable {
    public boolean isPickedUp();

    public void setPickedUp(boolean value);

    public boolean pickup(StringBuilder outString, List<AdvObject> inventory,
            List<AdvObject> roomObjects);
}
