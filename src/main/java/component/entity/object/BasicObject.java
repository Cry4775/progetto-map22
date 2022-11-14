package component.entity.object;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import component.entity.AbstractEntity;
import component.room.AbstractRoom;
import engine.Inventory;
import engine.database.DBManager;

public class BasicObject extends AbstractEntity {

    public BasicObject(ResultSet resultSet) throws SQLException {
        super(resultSet);
    }

    @Override
    public void saveOnDB() throws SQLException {
        PreparedStatement stm = DBManager.getConnection().prepareStatement(
                "INSERT INTO SAVEDATA.BasicObject values (?, ?, ?, ?, ?)");

        super.setKnownValuesOnStatement(stm);
        stm.executeUpdate();

        saveExternalsOnDB();
    }

    public static void loadFromDB(List<AbstractRoom> allRooms, Inventory inventory) throws SQLException {
        PreparedStatement stm =
                DBManager.getConnection()
                        .prepareStatement("SELECT * FROM SAVEDATA.BasicObject");
        ResultSet resultSet = stm.executeQuery();

        while (resultSet.next()) {
            BasicObject obj = new BasicObject(resultSet);

            obj.loadLocation(resultSet, allRooms, inventory);
            obj.loadObjEvents();
        }

        stm.close();
    }

}
