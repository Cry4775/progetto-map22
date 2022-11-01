package component.entity.pickupable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import com.google.common.collect.Multimap;
import component.entity.AbstractEntity;
import component.entity.container.AbstractContainer;
import component.entity.interfaces.ILightSource;
import component.room.AbstractRoom;
import component.room.PlayableRoom;

public class LightSourceItem extends BasicItem implements ILightSource {

    public LightSourceItem(ResultSet resultSet) throws SQLException {
        super(resultSet);
        on = resultSet.getBoolean(7);
        requiredItemId = resultSet.getString(8);
    }

    private boolean on = false;

    private BasicItem requiredItem;
    private String requiredItemId;

    @Override
    public boolean isOn() {
        return on;
    }

    @Override
    public void setOn(boolean value) {
        on = value;
    }

    public BasicItem getRequiredItem() {
        return requiredItem;
    }

    public void setRequiredItem(AbstractEntity requiredItem) {
        this.requiredItem = (BasicItem) requiredItem;
    }

    public String getRequiredItemId() {
        return requiredItemId;
    }

    public void setRequiredItemId(String requiredItemId) {
        this.requiredItemId = requiredItemId;
    }

    @Override
    public StringBuilder turnOn() {
        StringBuilder outString = new StringBuilder();

        if (!on) {

            if (requiredItem != null && requiredItem.isPickedUp()) {

                on = true;
                outString.append("Hai acceso: " + getName());

                setActionPerformed(true);
            } else if (requiredItem != null && !requiredItem.isPickedUp()) {
                outString.append("Non puoi farlo senza lo strumento adatto.");
            } else {
                on = true;
                outString.append("Hai acceso: " + getName());

                setActionPerformed(true);
            }
        } else {
            outString.append(getName() + " é giá acceso.");
        }
        return outString;
    }

    @Override
    public StringBuilder turnOff() {
        StringBuilder outString = new StringBuilder();

        if (on) {
            on = false;
            outString.append("Hai spento: " + getName());

            setActionPerformed(true);
        } else {
            outString.append(getName() + " é giá spento.");
        }
        return outString;
    }

    @Override
    public void processReferences(Multimap<String, AbstractEntity> objects,
            List<AbstractRoom> rooms) {
        super.processReferences(objects, rooms);

        if (requiredItemId != null) {
            if (!objects.containsKey(requiredItemId)) {
                throw new Error(
                        "Couldn't find the requested \"requiredItem\" ID on " + getName()
                                + " (" + getId()
                                + "). Check the JSON file for correct object IDs.");
            }

            for (AbstractEntity reqItem : objects.get(requiredItemId)) {
                requiredItem = (BasicItem) reqItem;
            }
        }
    }

    @Override
    public void saveOnDB(Connection connection) throws SQLException {
        PreparedStatement stm = connection.prepareStatement(
                "INSERT INTO SAVEDATA.LightSourceItem values (?, ?, ?, ?, ?, ?, ?, ?)");

        stm.setString(1, getId());
        stm.setString(2, getName());
        stm.setString(3, getDescription());

        if (getParent() instanceof PlayableRoom) {
            stm.setString(4, getClosestRoomParent().getId());
            stm.setString(5, "null");
        } else if (getParent() instanceof AbstractContainer) {
            stm.setString(4, "null");
            stm.setString(5, getParent().getId());
        }

        stm.setBoolean(6, isPickedUp());
        stm.setBoolean(7, on);
        stm.setString(8, requiredItemId);
        stm.executeUpdate();

        saveAliasesOnDB(connection);
        saveRequiredWearedItemsOnDB();
        saveEventsOnDB(connection);
    }

}
