package component.entity.pickupable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import component.entity.container.AbstractContainer;
import component.entity.interfaces.IWearable;
import component.event.EventType;
import component.room.AbstractRoom;
import engine.database.DBManager;

public class WearableItem extends BasicItem implements IWearable {

    public WearableItem(ResultSet resultSet) throws SQLException {
        super(resultSet);
        worn = resultSet.getBoolean(7);
    }

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
                "INSERT INTO SAVEDATA.WearableItem values (?, ?, ?, ?, ?, ?, ?)");

        setValuesOnStatement(stm);
        stm.setBoolean(7, worn);
        stm.executeUpdate();

        saveExternalsOnDB();
    }

    public static void loadFromDB(List<AbstractRoom> allRooms,
            List<AbstractContainer> allContainers) throws SQLException {
        PreparedStatement stm =
                DBManager.getConnection()
                        .prepareStatement("SELECT * FROM SAVEDATA.WearableItem");
        ResultSet resultSet = stm.executeQuery();

        while (resultSet.next()) {
            WearableItem obj = new WearableItem(resultSet);

            obj.loadLocation(resultSet, allRooms, allContainers);
        }

        stm.close();
    }
}
