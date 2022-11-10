package component.entity.pickupable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import component.entity.interfaces.IReadable;
import component.room.AbstractRoom;
import engine.OutputManager;
import engine.database.DBManager;

public class ReadableItem extends BasicItem implements IReadable {

    private String readText;

    public ReadableItem(ResultSet resultSet) throws SQLException {
        super(resultSet);
        readText = resultSet.getString(7);
    }

    @Override
    public void read() {
        if (!canInteract())
            return;

        if (readText != null && !readText.isEmpty()) {
            OutputManager.append(readText);
        } else {
            OutputManager.append("Non c'é scritto nulla.");
        }

        // TODO EVENTO
    }

    @Override
    public void saveOnDB() throws SQLException {
        PreparedStatement stm = DBManager.getConnection().prepareStatement(
                "INSERT INTO SAVEDATA.ReadableItem values (?, ?, ?, ?, ?, ?, ?)");

        setKnownValuesOnStatement(stm);
        stm.setString(7, readText);
        stm.executeUpdate();

        saveExternalsOnDB();
    }

    public static void loadFromDB(List<AbstractRoom> allRooms) throws SQLException {
        PreparedStatement stm =
                DBManager.getConnection()
                        .prepareStatement("SELECT * FROM SAVEDATA.ReadableItem");
        ResultSet resultSet = stm.executeQuery();

        while (resultSet.next()) {
            ReadableItem obj = new ReadableItem(resultSet);

            obj.loadLocation(resultSet, allRooms);
            obj.loadObjEvents();
        }

        stm.close();
    }

}
