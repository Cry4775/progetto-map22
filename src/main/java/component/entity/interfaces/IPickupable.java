package component.entity.interfaces;

import java.util.List;
import component.entity.AbstractEntity;

public interface IPickupable {
    public boolean isPickedUp();

    public void setPickedUp(boolean value);

    public StringBuilder pickup(List<AbstractEntity> inventory);
}
