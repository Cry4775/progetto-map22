package component.entity.object;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import component.entity.AbstractEntity;
import component.entity.interfaces.IPullable;
import component.event.EventType;
import component.room.AbstractRoom;
import engine.Inventory;
import engine.MoveInformations.ActionState;
import engine.database.DBManager;
import gui.GUIManager;
import sound.SoundManager;

public class PullableObject extends AbstractEntity implements IPullable {

    private boolean pulled = false;

    public PullableObject(ResultSet resultSet) throws SQLException {
        super(resultSet);
        pulled = resultSet.getBoolean(6);
    }

    @Override
    public boolean isPulled() {
        return pulled;
    }

    @Override
    public void setPulled(boolean value) {
        pulled = value;
    }

    @Override
    public ActionState pull() {
        if (!pulled) {
            if (!canInteract())
                return ActionState.NO_MOVE;

            pulled = true;

            SoundManager.playWav(SoundManager.PULL_SOUND_PATH, SoundManager.Channel.EFFECTS);
            GUIManager.appendOutput("Hai tirato: " + getName());
            triggerEvent(EventType.PULL);
            return ActionState.NORMAL_ACTION;
        } else {
            GUIManager.appendOutput("È stato già tirato.");
        }

        return ActionState.NO_MOVE;
    }

    @Override
    public void saveOnDB() throws SQLException {
        PreparedStatement stm = DBManager.getConnection().prepareStatement(
                "INSERT INTO SAVEDATA.PullableObject values (?, ?, ?, ?, ?, ?)");

        super.setKnownValuesOnStatement(stm);
        stm.setBoolean(6, pulled);
        stm.executeUpdate();

        saveExternalsOnDB();
    }

    /**
     * Loads all pullable objects from DB.
     * 
     * @param allRooms all the possible rooms list.
     * @param inventory the inventory reference.
     * @throws SQLException
     */
    public static void loadFromDB(List<AbstractRoom> allRooms, Inventory inventory) throws SQLException {
        PreparedStatement stm =
                DBManager.getConnection()
                        .prepareStatement("SELECT * FROM SAVEDATA.PullableObject");
        ResultSet resultSet = stm.executeQuery();

        while (resultSet.next()) {
            PullableObject obj = new PullableObject(resultSet);

            obj.loadLocation(resultSet, allRooms, inventory);
            obj.loadObjEvents();
        }

        stm.close();
    }

}
