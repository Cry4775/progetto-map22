package component.entity.container;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import component.event.ObjectEvent;

public class BasicContainer extends AbstractContainer {
    @Override
    public void saveOnDB(Connection connection) throws SQLException {
        PreparedStatement evtStm = connection.prepareStatement(
                "INSERT INTO SAVEDATA.ObjectEvent values (?, ?, ?)");

        for (ObjectEvent evt : getEvents()) {
            evtStm.setString(1, getId());
            evtStm.setString(2, evt.getEventType().toString());
            evtStm.setString(3, evt.getText());
            evtStm.executeUpdate();
        }
    }
}
