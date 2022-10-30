package component.entity.pickupable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import com.google.common.collect.Multimap;
import component.entity.AbstractEntity;
import component.entity.container.AbstractContainer;
import component.entity.interfaces.IFillable;
import component.event.ObjectEvent;
import component.room.AbstractRoom;
import component.room.PlayableRoom;

public class FillableItem extends BasicItem implements IFillable {

    private boolean filled;

    private AbstractEntity eligibleItem;
    private String eligibleItemId;

    public boolean isFilled() {
        return filled;
    }

    public void setFilled(boolean value) {
        filled = value;
    }

    public AbstractEntity getEligibleItem() {
        return eligibleItem;
    }

    public void setEligibleItem(AbstractEntity eligibleItem) {
        this.eligibleItem = eligibleItem;
    }

    public String getEligibleItemId() {
        return eligibleItemId;
    }

    public void setEligibleItemId(String eligibleItemId) {
        this.eligibleItemId = eligibleItemId;
    }

    @Override
    public boolean fill(AbstractEntity obj) {
        if (eligibleItem.equals(obj)) {
            filled = true;
            // TODO distinzione tra riempi e prendi
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void processReferences(Multimap<String, AbstractEntity> objects,
            List<AbstractRoom> rooms) {
        super.processReferences(objects, rooms);

        if (eligibleItemId != null) {
            if (!objects.containsKey(eligibleItemId)) {
                throw new Error(
                        "Couldn't find the requested \"eligibleItem\" ID on " + getName()
                                + " (" + getId()
                                + "). Check the JSON file for correct object IDs.");
            }

            for (AbstractEntity reqItem : objects.get(eligibleItemId)) {
                eligibleItem = reqItem;
            }
        }
    }

    @Override
    public void saveOnDB(Connection connection) throws SQLException {
        PreparedStatement stm = connection.prepareStatement(
                "INSERT INTO SAVEDATA.FillableItem values (?, ?, ?, ?, ?)");
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
        stm.setBoolean(5, filled);
        stm.executeUpdate();

        for (ObjectEvent evt : getEvents()) {
            evtStm.setString(1, getId());
            evtStm.setString(2, evt.getEventType().toString());
            evtStm.setString(3, evt.getText());
            evtStm.executeUpdate();
        }
    }

}
