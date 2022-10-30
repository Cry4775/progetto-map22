package component.entity.pickupable;

import java.util.List;
import com.google.common.collect.Multimap;
import component.entity.AbstractEntity;
import component.entity.container.AbstractContainer;
import component.entity.interfaces.IPickupable;
import component.event.EventType;
import component.room.AbstractRoom;
import component.room.PlayableRoom;
import sound.SoundManager;
import sound.SoundManager.Mode;

public class BasicItem extends AbstractEntity implements IPickupable {

    private String inventoryDescription; // TODO implementare

    private boolean pickedUp = false;

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
    public void processReferences(Multimap<String, AbstractEntity> objects,
            List<AbstractRoom> rooms) {
        processRoomParent(rooms);
        processEventReferences(objects, rooms);
    }
}
