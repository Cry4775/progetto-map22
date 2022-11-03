package component.entity.object;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import com.google.common.collect.Multimap;
import component.entity.AbstractEntity;
import component.entity.container.AbstractContainer;
import component.room.AbstractRoom;
import engine.database.DBManager;

public class BasicObject extends AbstractEntity {

    public BasicObject(ResultSet resultSet) throws SQLException {
        super(resultSet);
    }

    @Override
    public void processReferences(Multimap<String, AbstractEntity> objects,
            List<AbstractRoom> rooms) {
        processRoomParent(rooms);
        processEventReferences(objects, rooms);
    }

    @Override
    public void saveOnDB(Connection connection) throws SQLException {
        PreparedStatement stm = connection.prepareStatement(
                "INSERT INTO SAVEDATA.BasicObject values (?, ?, ?, ?, ?)");

        super.setValuesOnStatement(stm);
        stm.executeUpdate();

        saveExternalsOnDB();
    }

    public static void loadFromDB(List<AbstractRoom> allRooms,
            List<AbstractContainer> allContainers) throws SQLException {
        PreparedStatement stm =
                DBManager.getConnection()
                        .prepareStatement("SELECT * FROM SAVEDATA.BasicObject");
        ResultSet resultSet = stm.executeQuery();

        while (resultSet.next()) {
            BasicObject obj = new BasicObject(resultSet);

            obj.loadLocation(resultSet, allRooms, allContainers);
        }

        stm.close();
    }

}
