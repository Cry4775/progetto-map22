package component.entity.doorlike;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import component.entity.AbstractEntity;
import component.room.AbstractRoom;
import engine.Inventory;
import engine.database.DBManager;

public class UnopenableDoor extends AbstractEntity {

    private String openEventText;

    public UnopenableDoor(ResultSet resultSet) throws SQLException {
        super(resultSet);
        openEventText = resultSet.getString(6);
    }

    public String getOpenEventText() {
        return openEventText;
    }

    @Override
    public void saveOnDB() throws SQLException {
        PreparedStatement stm = DBManager.getConnection().prepareStatement(
                "INSERT INTO SAVEDATA.UnopenableDoor values (?, ?, ?, ?, ?, ?)");

        setKnownValuesOnStatement(stm);
        stm.setString(6, openEventText);
        stm.executeUpdate();

        saveExternalsOnDB();
    }

    /**
     * Loads all unopenable door objects from DB.
     * 
     * @param allRooms all the possible rooms list.
     * @param inventory the inventory reference.
     * @throws SQLException
     */
    public static void loadFromDB(List<AbstractRoom> allRooms, Inventory inventory) throws SQLException {
        PreparedStatement stm = DBManager.getConnection()
                .prepareStatement("SELECT * FROM SAVEDATA.UnopenableDoor");
        ResultSet resultSet = stm.executeQuery();

        while (resultSet.next()) {
            UnopenableDoor obj = new UnopenableDoor(resultSet);

            obj.loadLocation(resultSet, allRooms, inventory);
            obj.loadObjEvents();
        }

        stm.close();
    }

}
