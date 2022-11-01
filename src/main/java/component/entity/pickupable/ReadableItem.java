package component.entity.pickupable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import component.entity.container.AbstractContainer;
import component.entity.interfaces.IReadable;
import component.room.PlayableRoom;

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

        stm.setBoolean(6, isPickedUp());
        stm.setString(7, readText);
        stm.executeUpdate();

        saveAliasesOnDB(connection);
        saveEventsOnDB(connection);
    }

}
