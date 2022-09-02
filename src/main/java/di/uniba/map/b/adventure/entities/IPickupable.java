package di.uniba.map.b.adventure.entities;

import java.util.List;
import di.uniba.map.b.adventure.games.Status;

public interface IPickupable {
    public boolean isPickedUp();

    public void setPickedUp(boolean value);

    public StringBuilder pickup(Status status,
            List<AbstractEntity> inventory,
            List<AbstractEntity> roomObjects);
}
