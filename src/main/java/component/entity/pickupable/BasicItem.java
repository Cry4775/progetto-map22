package component.entity.pickupable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import com.google.common.collect.Multimap;
import component.entity.AbstractEntity;
import component.entity.container.AbstractContainer;
import component.entity.interfaces.IPickupable;
import component.event.EventType;
import component.event.ObjectEvent;
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

    @Override
    public void saveOnDB(Connection connection) throws SQLException {
        PreparedStatement stm = connection.prepareStatement(
                "INSERT INTO SAVEDATA.BasicItem values (?, ?, ?, ?)");
        PreparedStatement evtStm = connection.prepareStatement(
                "INSERT INTO SAVEDATA.ObjectEvent values (?, ?, ?)");

        stm.setString(1, getId());

        if (getParent() instanceof PlayableRoom) {
            stm.setString(3, getParent().getId());
            stm.setString(4, "null");
        } else if (getParent() instanceof AbstractContainer) {
            stm.setString(3, "null");
            stm.setString(4, getParent().getId());
        }

        stm.setBoolean(2, pickedUp);
        stm.executeUpdate();

        for (ObjectEvent evt : getEvents()) {
            evtStm.setString(1, getId());
            evtStm.setString(2, evt.getEventType().toString());
            evtStm.setString(3, evt.getText());
            evtStm.executeUpdate();
        }
    }
}
