package component.event;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RoomEvent extends AbstractEvent {

    public RoomEvent(ResultSet resultSet) throws SQLException {
        super(resultSet);
    }

}
