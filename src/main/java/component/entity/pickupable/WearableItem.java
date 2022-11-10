package component.entity.pickupable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import component.entity.interfaces.IWearable;
import component.event.EventType;
import component.room.AbstractRoom;
import engine.OutputManager;
import engine.database.DBManager;

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
    public void wear() {
        if (!worn) {
            worn = true;

            OutputManager.append("Hai indossato: " + getName());
            triggerEvent((EventType.WEAR));

            setActionPerformed(true);
        } else {
            OutputManager.append("L'hai giá indossato.");
        }
    }

    @Override
    public void unwear() {
        if (worn) {
            worn = false;

            OutputManager.append("Hai tolto: " + getName());
            triggerEvent((EventType.WEAR));

            setActionPerformed(true);
        } else {
            OutputManager.append("Non ce l'hai addosso.");
        }
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

    public static void loadFromDB(List<AbstractRoom> allRooms) throws SQLException {
        PreparedStatement stm =
                DBManager.getConnection()
                        .prepareStatement("SELECT * FROM SAVEDATA.WearableItem");
        ResultSet resultSet = stm.executeQuery();

        while (resultSet.next()) {
            WearableItem obj = new WearableItem(resultSet);

            obj.loadLocation(resultSet, allRooms);
            obj.loadObjEvents();
        }

        stm.close();
    }
}
