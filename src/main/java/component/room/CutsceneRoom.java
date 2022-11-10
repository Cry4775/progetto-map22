package component.room;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import engine.database.DBManager;

public class CutsceneRoom extends AbstractRoom {

    private AbstractRoom nextRoom;
    private String nextRoomId;

    private boolean finalRoom = false;

    public CutsceneRoom(ResultSet resultSet) throws SQLException {
        super(resultSet);
        nextRoomId = resultSet.getString(5);
        finalRoom = resultSet.getBoolean(6);
    }

    public boolean isFinalRoom() {
        return finalRoom;
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

    @Override
    public void saveOnDB() throws SQLException {
        PreparedStatement stm = DBManager.getConnection().prepareStatement(
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
