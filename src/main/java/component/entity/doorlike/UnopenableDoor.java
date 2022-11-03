package component.entity.doorlike;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import com.google.common.collect.Multimap;
import component.entity.AbstractEntity;
import component.entity.container.AbstractContainer;
import component.room.AbstractRoom;
import component.room.PlayableRoom;
import engine.database.DBManager;

public class UnopenableDoor extends AbstractEntity {

    public UnopenableDoor(ResultSet resultSet) throws SQLException {
        super(resultSet);
        openEventText = resultSet.getString(6);
    }

    private String openEventText; // TODO porting a evento

    public String getOpenEventText() {
        return openEventText;
    }

    public void setOpenEventText(String openEventText) {
        this.openEventText = openEventText;
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
                "INSERT INTO SAVEDATA.UnopenableDoor values (?, ?, ?, ?, ?, ?)");

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

        stm.setString(6, openEventText);
        stm.executeUpdate();

        saveExternalsOnDB(connection);
    }

    public static void loadFromDB(List<AbstractRoom> allRooms,
            List<AbstractContainer> allContainers) throws SQLException {
        PreparedStatement stm =
                DBManager.getConnection()
                        .prepareStatement("SELECT * FROM SAVEDATA.UnopenableDoor");
        ResultSet resultSet = stm.executeQuery();

        while (resultSet.next()) {
            UnopenableDoor obj = new UnopenableDoor(resultSet);

            obj.loadLocation(resultSet, allRooms, allContainers);
        }

        stm.close();
    }

}
