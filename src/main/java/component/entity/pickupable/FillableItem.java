package component.entity.pickupable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import com.google.common.collect.Multimap;
import component.entity.AbstractEntity;
import component.entity.container.AbstractContainer;
import component.entity.interfaces.IFillable;
import component.room.AbstractRoom;
import engine.database.DBManager;

public class FillableItem extends BasicItem implements IFillable {

    public FillableItem(ResultSet resultSet) throws SQLException {
        super(resultSet);
        filled = resultSet.getBoolean(7);
        eligibleItemId = resultSet.getString(8);
    }

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
                "INSERT INTO SAVEDATA.FillableItem values (?, ?, ?, ?, ?, ?, ?, ?)");

        setValuesOnStatement(stm);
        stm.setBoolean(7, filled);
        stm.setString(8, eligibleItemId);
        stm.executeUpdate();

        saveExternalsOnDB();
    }

    public static void loadFromDB(List<AbstractRoom> allRooms,
            List<AbstractContainer> allContainers) throws SQLException {
        PreparedStatement stm =
                DBManager.getConnection()
                        .prepareStatement("SELECT * FROM SAVEDATA.FillableItem");
        ResultSet resultSet = stm.executeQuery();

        while (resultSet.next()) {
            FillableItem obj = new FillableItem(resultSet);

            obj.loadLocation(resultSet, allRooms, allContainers);
            obj.loadObjEvents();
        }

        stm.close();
    }

}
