package component.entity.container;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import component.entity.AbstractEntity;
import component.room.AbstractRoom;
import engine.database.DBManager;
import utility.Triple;

public class BasicContainer extends AbstractContainer {
    public BasicContainer(ResultSet resultSet) throws SQLException {
        super(resultSet);
        setForFluids(resultSet.getBoolean(6));
    }

    @Override
    public void saveOnDB(Connection connection) throws SQLException {
        PreparedStatement stm = connection.prepareStatement(
                "INSERT INTO SAVEDATA.BasicContainer values (?, ?, ?, ?, ?, ?)");

        setValuesOnStatement(stm);
        stm.setBoolean(6, isForFluids());

        stm.executeUpdate();

        saveExternalsOnDB();
    }

    public static void loadFromDB(List<AbstractRoom> allRooms,
            List<Triple<AbstractEntity, String, String>> pendingList) throws SQLException {
        PreparedStatement stm =
                DBManager.getConnection().prepareStatement("SELECT * FROM SAVEDATA.BasicContainer");
        ResultSet resultSet = stm.executeQuery();

        while (resultSet.next()) {
            BasicContainer obj = new BasicContainer(resultSet);

            Triple<AbstractEntity, String, String> pending = obj.loadLocation(resultSet, allRooms);
            obj.loadObjEvents();

            if (pending != null)
                pendingList.add(pending);
        }

        stm.close();
    }
}
