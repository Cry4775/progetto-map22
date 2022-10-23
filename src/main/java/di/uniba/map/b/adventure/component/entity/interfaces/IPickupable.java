package di.uniba.map.b.adventure.component.entity.interfaces;

import java.util.List;
import di.uniba.map.b.adventure.component.entity.AbstractEntity;

public interface IPickupable {
    public boolean isPickedUp();

    public void setPickedUp(boolean value);

    public StringBuilder pickup(List<AbstractEntity> inventory);
}
