package component.event;

import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class AbstractEvent {
    private EventType eventType;

    private String text;

    private boolean triggered;

    public AbstractEvent(ResultSet resultSet) throws SQLException {
        eventType = EventType.valueOf(resultSet.getString(2));
        text = resultSet.getString(3);
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isTriggered() {
        return triggered;
    }

    public void setTriggered(boolean triggered) {
        this.triggered = triggered;
    }
}
