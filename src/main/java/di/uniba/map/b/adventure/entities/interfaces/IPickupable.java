package di.uniba.map.b.adventure.entities.interfaces;

import java.util.List;
import di.uniba.map.b.adventure.entities.AbstractEntity;

public interface IPickupable {
    public boolean isPickedUp();

    public void setPickedUp(boolean value);

    public StringBuilder pickup(List<AbstractEntity> inventory);
}
