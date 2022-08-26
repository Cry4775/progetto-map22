package di.uniba.map.b.adventure.type;

import java.util.List;

public interface IPickupable {
    public boolean isPicked();

    public void setPicked(boolean value);

    public boolean pickUp(StringBuilder outString, List<AdvObject> inventory,
            List<AdvObject> roomObjects);
}
