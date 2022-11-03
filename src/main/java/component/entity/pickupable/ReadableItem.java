package component.entity.pickupable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import component.entity.container.AbstractContainer;
import component.entity.interfaces.IReadable;
import component.room.AbstractRoom;
import engine.database.DBManager;

public class ReadableItem extends BasicItem implements IReadable {

    public ReadableItem(ResultSet resultSet) throws SQLException {
        super(resultSet);
        readText = resultSet.getString(7);
    }

    private String readText;

    public void setReadText(String readText) {
        this.readText = readText;
    }

    @Override
    public StringBuilder read() {
        StringBuilder outString = new StringBuilder();

        if (readText != null && !readText.isEmpty()) {
            outString.append(readText);
        } else {
            outString.append("Non c'é scritto nulla.");
        }

        return outString;
    }

    @Override
    public void saveOnDB(Connection connection) throws SQLException {
        PreparedStatement stm = connection.prepareStatement(
                "INSERT INTO SAVEDATA.ReadableItem values (?, ?, ?, ?, ?, ?, ?)");

        setValuesOnStatement(stm);
        stm.setString(7, readText);
        stm.executeUpdate();

        saveExternalsOnDB();
    }

    public static void loadFromDB(List<AbstractRoom> allRooms,
            List<AbstractContainer> allContainers) throws SQLException {
        PreparedStatement stm =
                DBManager.getConnection()
                        .prepareStatement("SELECT * FROM SAVEDATA.ReadableItem");
        ResultSet resultSet = stm.executeQuery();

        while (resultSet.next()) {
            ReadableItem obj = new ReadableItem(resultSet);

            obj.loadLocation(resultSet, allRooms, allContainers);
        }

        stm.close();
    }

}
