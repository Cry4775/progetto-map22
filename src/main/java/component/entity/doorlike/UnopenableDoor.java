package component.entity.doorlike;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import com.google.common.collect.Multimap;
import component.entity.AbstractEntity;
import component.room.AbstractRoom;
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

        setKnownValuesOnStatement(stm);
        stm.setString(6, openEventText);
        stm.executeUpdate();

        saveExternalsOnDB();
    }

    public static void loadFromDB(List<AbstractRoom> allRooms) throws SQLException {
        PreparedStatement stm =
                DBManager.getConnection()
                        .prepareStatement("SELECT * FROM SAVEDATA.UnopenableDoor");
        ResultSet resultSet = stm.executeQuery();

        while (resultSet.next()) {
            UnopenableDoor obj = new UnopenableDoor(resultSet);

            obj.loadLocation(resultSet, allRooms);
            obj.loadObjEvents();
        }

        stm.close();
    }

}
