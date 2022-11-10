package component.entity.pickupable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import com.google.common.collect.Multimap;
import component.entity.AbstractEntity;
import component.entity.interfaces.ILightSource;
import component.room.AbstractRoom;
import engine.OutputManager;
import engine.database.DBManager;

public class LightSourceItem extends BasicItem implements ILightSource {

    private boolean on = false;

    private BasicItem requiredItem;
    private String requiredItemId;

    public LightSourceItem(ResultSet resultSet) throws SQLException {
        super(resultSet);
        on = resultSet.getBoolean(7);
        requiredItemId = resultSet.getString(8);
    }

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
    public void turnOn() {
        if (!on) {
            if (!canInteract())
                return;

            if (requiredItem != null && !requiredItem.isPickedUp()) {
                OutputManager.append("Non puoi farlo senza lo strumento adatto.");
            } else {
                on = true;
                OutputManager.append("Hai acceso: " + getName());
                // TODO EVENTO

                setActionPerformed(true);
            }
        } else {
            OutputManager.append(getName() + " é giá acceso.");
        }
    }

    @Override
    public void turnOff() {
        if (on) {
            on = false;
            OutputManager.append("Hai spento: " + getName());

            setActionPerformed(true);
        } else {
            OutputManager.append(getName() + " é giá spento.");
        }
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
    public void saveOnDB() throws SQLException {
        PreparedStatement stm = DBManager.getConnection().prepareStatement(
                "INSERT INTO SAVEDATA.LightSourceItem values (?, ?, ?, ?, ?, ?, ?, ?)");

        setKnownValuesOnStatement(stm);
        stm.setBoolean(7, on);
        stm.setString(8, requiredItemId);
        stm.executeUpdate();

        saveExternalsOnDB();
    }

    public static void loadFromDB(List<AbstractRoom> allRooms) throws SQLException {
        PreparedStatement stm =
                DBManager.getConnection()
                        .prepareStatement("SELECT * FROM SAVEDATA.LightSourceItem");
        ResultSet resultSet = stm.executeQuery();

        while (resultSet.next()) {
            LightSourceItem obj = new LightSourceItem(resultSet);

            obj.loadLocation(resultSet, allRooms);
            obj.loadObjEvents();
        }

        stm.close();
    }

}
