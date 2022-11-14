package component.entity.pickupable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import component.entity.interfaces.IWearable;
import component.event.EventType;
import component.room.AbstractRoom;
import engine.Inventory;
import engine.database.DBManager;
import gui.GUIManager;

public class WearableItem extends BasicItem implements IWearable {

    private boolean worn = false;

    public WearableItem(ResultSet resultSet) throws SQLException {
        super(resultSet);
        worn = resultSet.getBoolean(7);
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
    public boolean wear() {
        if (!worn) {
            if (!canInteract())
                return false;

            worn = true;

            GUIManager.appendOutput("Hai indossato: " + getName());
            triggerEvent(EventType.WEAR);
            return true;
        } else {
            GUIManager.appendOutput("L'hai giá indossato.");
        }

        return false;
    }

    @Override
    public boolean unwear() {
        if (worn) {
            worn = false;

            GUIManager.appendOutput("Hai tolto: " + getName());
            triggerEvent((EventType.WEAR));
            return true;
        } else {
            GUIManager.appendOutput("Non ce l'hai addosso.");
        }

        return false;
    }

    @Override
    public void saveOnDB() throws SQLException {
        PreparedStatement stm = DBManager.getConnection().prepareStatement(
                "INSERT INTO SAVEDATA.WearableItem values (?, ?, ?, ?, ?, ?, ?)");

        setKnownValuesOnStatement(stm);
        stm.setBoolean(7, worn);
        stm.executeUpdate();

        saveExternalsOnDB();
    }

    public static void loadFromDB(List<AbstractRoom> allRooms, Inventory inventory) throws SQLException {
        PreparedStatement stm =
                DBManager.getConnection()
                        .prepareStatement("SELECT * FROM SAVEDATA.WearableItem");
        ResultSet resultSet = stm.executeQuery();

        while (resultSet.next()) {
            WearableItem obj = new WearableItem(resultSet);

            obj.loadLocation(resultSet, allRooms, inventory);
            obj.loadObjEvents();
        }

        stm.close();
    }
}
