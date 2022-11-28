package component.entity.object;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import component.entity.AbstractEntity;
import component.entity.interfaces.IMovable;
import component.event.EventType;
import component.room.AbstractRoom;
import engine.Inventory;
import engine.MoveInformations.ActionState;
import engine.database.DBManager;
import gui.GUIManager;

public class MovableObject extends AbstractEntity implements IMovable {

    private boolean moved = false;

    public MovableObject(ResultSet resultSet) throws SQLException {
        super(resultSet);
        moved = resultSet.getBoolean(6);
    }

    @Override
    public boolean isMoved() {
        return moved;
    }

    @Override
    public void setMoved(boolean value) {
        moved = value;
    }

    @Override
    public ActionState move() {
        if (!moved) {
            if (!canInteract())
                return ActionState.NO_MOVE;

            moved = true;
            GUIManager.appendOutput("Hai spostato: " + getName());
            triggerEvent(EventType.MOVE);
            return ActionState.NORMAL_ACTION;
        } else {
            GUIManager.appendOutput("È stato già spostato.");
        }

        return ActionState.NO_MOVE;
    }

    @Override
    public void saveOnDB() throws SQLException {
        PreparedStatement stm = DBManager.getConnection().prepareStatement(
                "INSERT INTO SAVEDATA.MovableObject values (?, ?, ?, ?, ?, ?)");

        super.setKnownValuesOnStatement(stm);
        stm.setBoolean(6, moved);
        stm.executeUpdate();

        saveExternalsOnDB();
    }

    public static void loadFromDB(List<AbstractRoom> allRooms, Inventory inventory) throws SQLException {
        PreparedStatement stm =
                DBManager.getConnection()
                        .prepareStatement("SELECT * FROM SAVEDATA.MovableObject");
        ResultSet resultSet = stm.executeQuery();

        while (resultSet.next()) {
            MovableObject obj = new MovableObject(resultSet);

            obj.loadLocation(resultSet, allRooms, inventory);
            obj.loadObjEvents();
        }

        stm.close();
    }

}
