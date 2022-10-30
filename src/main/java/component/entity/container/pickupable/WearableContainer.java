package component.entity.container.pickupable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import component.entity.AbstractEntity;
import component.entity.container.AbstractContainer;
import component.entity.interfaces.IWearable;
import component.event.EventType;
import component.event.ObjectEvent;
import component.room.PlayableRoom;

public class WearableContainer extends AbstractContainer implements IWearable {

    private boolean worn = false;
    private boolean pickedUp = false;
    private int maxSlots;

    public int getMaxSlots() {
        return maxSlots;
    }

    public void setMaxSlots(int maxSlots) {
        this.maxSlots = maxSlots;
    }

    @Override
    public boolean isWorn() {
        return worn;
    }

    @Override
    public void setWorn(boolean value) {
        this.worn = value;
    }

    @Override
    public boolean isPickedUp() {
        return pickedUp;
    }

    @Override
    public void setPickedUp(boolean value) {
        pickedUp = value;
    }

    @Override
    public StringBuilder wear() {
        StringBuilder outString = new StringBuilder();

        if (!worn) {
            worn = true;

            outString.append("Hai indossato: " + getName());
            outString.append(processEvent(EventType.UNWEAR));

            setActionPerformed(true);
        } else {
            outString.append("L'hai giá indossato.");
        }
        return outString;
    }

    @Override
    public StringBuilder unwear() {
        StringBuilder outString = new StringBuilder();

        if (worn) {
            worn = false;

            outString.append("Hai tolto: " + getName());
            outString.append(processEvent(EventType.UNWEAR));

            setActionPerformed(true);
        } else {
            outString.append("Non ce l'hai addosso.");
        }
        return outString;
    }

    @Override
    public StringBuilder pickup(List<AbstractEntity> inventory) {
        StringBuilder outString = new StringBuilder();

        pickedUp = true;
        inventory.add(this);

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

        outString.append("Hai raccolto: " + getName());
        outString.append(processEvent(EventType.PICK_UP));

        setActionPerformed(true);
        return outString;
    }

    @Override
    public StringBuilder insert(AbstractEntity obj, List<AbstractEntity> inventory) {
        StringBuilder outString = new StringBuilder();

        if (maxSlots == 0) {
            maxSlots = 999;
        }

        if (getList().size() < maxSlots) {
            if (obj instanceof IWearable) {
                IWearable wearable = (IWearable) obj;

                if (wearable.isWorn()) {
                    outString.append("Devi prima toglierlo di dosso.");
                    return outString;
                }
            }

            obj.setParent(this);
            inventory.remove(obj);

            this.add(obj);

            outString.append("Hai lasciato: " + obj.getName());
            outString.append(processEvent(EventType.INSERT));

            setActionPerformed(true);
        } else {
            outString.append("Non ci entra piú nulla. Libera spazio o tienilo nell'inventario!");
        }
        return outString;
    }

    @Override
    public void saveOnDB(Connection connection) throws SQLException {
        PreparedStatement stm = connection.prepareStatement(
                "INSERT INTO SAVEDATA.WearableContainer values (?, ?, ?, ?, ?)");
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
        stm.setBoolean(5, worn);
        stm.executeUpdate();

        for (ObjectEvent evt : getEvents()) {
            evtStm.setString(1, getId());
            evtStm.setString(2, evt.getEventType().toString());
            evtStm.setString(3, evt.getText());
            evtStm.executeUpdate();
        }
    }

}
