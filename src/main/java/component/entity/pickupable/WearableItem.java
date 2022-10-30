package component.entity.pickupable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import component.entity.container.AbstractContainer;
import component.entity.interfaces.IWearable;
import component.event.EventType;
import component.event.ObjectEvent;
import component.room.PlayableRoom;

public class WearableItem extends BasicItem implements IWearable {

    private boolean worn = false;

    @Override
    public boolean isWorn() {
        return worn;
    }

    @Override
    public void setWorn(boolean value) {
        this.worn = value;
    }

    @Override
    public StringBuilder wear() {
        StringBuilder outString = new StringBuilder();

        if (!worn) {
            worn = true;

            outString.append("Hai indossato: " + getName());
            outString.append(processEvent(EventType.WEAR));

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
            outString.append(processEvent(EventType.WEAR));

            setActionPerformed(true);
        } else {
            outString.append("Non ce l'hai addosso.");
        }
        return outString;
    }

    @Override
    public void saveOnDB(Connection connection) throws SQLException {
        PreparedStatement stm = connection.prepareStatement(
                "INSERT INTO SAVEDATA.WearableItem values (?, ?, ?, ?, ?)");
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

        stm.setBoolean(2, isPickedUp());
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
