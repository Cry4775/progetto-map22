package component.entity.container;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import component.room.PlayableRoom;

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
}
