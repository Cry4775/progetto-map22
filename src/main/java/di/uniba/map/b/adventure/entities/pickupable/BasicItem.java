package di.uniba.map.b.adventure.entities.pickupable;

import java.util.List;
import java.util.Set;
import com.google.common.collect.Multimap;
import di.uniba.map.b.adventure.SoundManager;
import di.uniba.map.b.adventure.SoundManager.Mode;
import di.uniba.map.b.adventure.entities.AbstractEntity;
import di.uniba.map.b.adventure.entities.container.AbstractContainer;
import di.uniba.map.b.adventure.entities.interfaces.IPickupable;
import di.uniba.map.b.adventure.type.EventType;
import di.uniba.map.b.adventure.type.PlayableRoom;
import di.uniba.map.b.adventure.type.AbstractRoom;

public class BasicItem extends AbstractEntity implements IPickupable {

    private String inventoryDescription; // TODO implementare

    private boolean pickedUp = false;

    public BasicItem(int id, String name, String description) {
        super(id, name, description);
    }

    public BasicItem(int id, String name, String description, Set<String> alias) {
        super(id, name, description, alias);
    }

    public String getInventoryDescription() {
        return inventoryDescription;
    }

    public void setInventoryDescription(String inventoryDescription) {
        this.inventoryDescription = inventoryDescription;
    }

    public boolean isPickedUp() {
        return pickedUp;
    }

    public void setPickedUp(boolean value) {
        pickedUp = value;
    }

    @Override
    public StringBuilder pickup(List<AbstractEntity> inventory) {

        StringBuilder outString = new StringBuilder();

        if (!pickedUp) {
            // Check if it's an obj inside something and remove it from its list
            if (getParent() != null) {
                if (getParent() instanceof AbstractContainer) {
                    AbstractContainer parentContainer = (AbstractContainer) getParent();
                    parentContainer.getList().remove(this);
                    setParent(null);
                } else if (getParent() instanceof PlayableRoom) {
                    PlayableRoom room = (PlayableRoom) getParent();
                    room.getObjects().remove(this);
                }
            }

            inventory.add(this);
            SoundManager.playWav(SoundManager.PICKUP_SOUND_PATH, Mode.SOUND);
            pickedUp = true;

            outString.append("Hai raccolto: " + getName());
            outString.append(processEvent(EventType.PICK_UP));

            setActionPerformed(true);
        } else {
            outString.append("É giá nel tuo inventario.");
        }

        return outString;
    }

    @Override
    public void processReferences(Multimap<Integer, AbstractEntity> objects,
            List<AbstractRoom> rooms) {
        processRoomParent(rooms);
        processEventReferences(objects, rooms);
    }
}
