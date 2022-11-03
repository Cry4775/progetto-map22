package component.entity.container;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import component.entity.AbstractEntity;
import component.room.AbstractRoom;
import component.room.PlayableRoom;
import engine.database.DBManager;
import utility.Pair;

public class BasicContainer extends AbstractContainer {
    public BasicContainer(ResultSet resultSet) throws SQLException {
        super(resultSet);
        setForFluids(resultSet.getBoolean(6));
    }

    @Override
    public void saveOnDB(Connection connection) throws SQLException {
        PreparedStatement stm = connection.prepareStatement(
                "INSERT INTO SAVEDATA.BasicContainer values (?, ?, ?, ?, ?, ?)");

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

        stm.setBoolean(6, isForFluids());

        stm.executeUpdate();

        saveExternalsOnDB(connection);
    }

    public static void loadFromDB(List<AbstractRoom> allRooms,
            List<Pair<AbstractEntity, String>> pendingList) throws SQLException {
        PreparedStatement stm =
                DBManager.getConnection().prepareStatement("SELECT * FROM SAVEDATA.BasicContainer");
        ResultSet resultSet = stm.executeQuery();

        while (resultSet.next()) {
            BasicContainer obj = new BasicContainer(resultSet);

            Pair<AbstractEntity, String> pending = obj.loadLocation(resultSet, allRooms);

            if (pending != null)
                pendingList.add(pending);
        }

        stm.close();
    }
}
