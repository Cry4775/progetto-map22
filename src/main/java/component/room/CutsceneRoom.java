package component.room;

import java.sql.Connection;
import java.sql.SQLException;

public class CutsceneRoom extends AbstractRoom {
    private AbstractRoom nextRoom;
    private String nextRoomId;

    private boolean finalRoom = false;

    public boolean isFinalRoom() {
        return finalRoom;
    }

    public void setFinalRoom(boolean finalRoom) {
        this.finalRoom = finalRoom;
    }

    public AbstractRoom getNextRoom() {
        return nextRoom;
    }

    public void setNextRoom(AbstractRoom nextRoom) {
        this.nextRoom = nextRoom;
    }

    public String getNextRoomId() {
        return nextRoomId;
    }

    public void setNextRoomId(String nextRoomId) {
        this.nextRoomId = nextRoomId;
    }

    @Override
    public void saveOnDB(Connection connection) throws SQLException {

    }
}
