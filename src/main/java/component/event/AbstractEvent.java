package component.event;

import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class AbstractEvent {
    private EventType eventType;

    private String text;

    private boolean triggered;

    protected AbstractEvent(ResultSet resultSet) throws SQLException {
        eventType = EventType.valueOf(resultSet.getString(3));
        text = resultSet.getString(4);
    }

    public EventType getEventType() {
        return eventType;
    }

    public String getText() {
        return text;
    }

    public boolean isTriggered() {
        return triggered;
    }

    public void setTriggered(boolean triggered) {
        this.triggered = triggered;
    }
}
