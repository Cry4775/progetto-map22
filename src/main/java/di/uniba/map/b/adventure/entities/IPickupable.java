package di.uniba.map.b.adventure.entities;

import java.util.List;

public interface IPickupable {
    public boolean isPickedUp();

    public void setPickedUp(boolean value);

    public StringBuilder pickup(List<AbstractEntity> inventory, List<AbstractEntity> roomObjects);
}
