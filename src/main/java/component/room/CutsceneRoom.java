package component.room;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CutsceneRoom extends AbstractRoom {
    public CutsceneRoom(ResultSet resultSet) throws SQLException {
        super(resultSet);
        nextRoomId = resultSet.getString(5);
        finalRoom = resultSet.getBoolean(6);
    }

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
        PreparedStatement stm = connection.prepareStatement(
                "INSERT INTO SAVEDATA.CutsceneRoom values (?, ?, ?, ?, ?, ?)");
        stm.setString(1, getId());
        stm.setString(2, getName());
        stm.setString(3, getDescription());
        stm.setString(4, getImgPath());
        stm.setString(5, nextRoomId);
        stm.setBoolean(6, finalRoom);
        stm.executeUpdate();
    }
}
