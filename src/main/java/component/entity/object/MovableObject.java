package component.entity.object;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import component.entity.AbstractEntity;
import component.entity.interfaces.IMovable;
import component.entity.interfaces.IWearable;
import component.event.EventType;
import component.room.AbstractRoom;
import engine.OutputManager;
import engine.database.DBManager;

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
    public void move() {
        if (!moved) {
            if (getRequiredWearedItemsToInteract() != null) {
                for (IWearable wearable : getRequiredWearedItemsToInteract()) {
                    if (!wearable.isWorn()) {
                        OutputManager.append(getFailedInteractionMessage());
                        return;
                    }
                }
            }

            moved = true;
            OutputManager.append("Hai spostato: " + getName());
            triggerEvent((EventType.MOVE));

            setActionPerformed(true);
        } else {
            OutputManager.append("È stato già spostato.");
        }
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

    public static void loadFromDB(List<AbstractRoom> allRooms) throws SQLException {
        PreparedStatement stm =
                DBManager.getConnection()
                        .prepareStatement("SELECT * FROM SAVEDATA.MovableObject");
        ResultSet resultSet = stm.executeQuery();

        while (resultSet.next()) {
            MovableObject obj = new MovableObject(resultSet);

            obj.loadLocation(resultSet, allRooms);
            obj.loadObjEvents();
        }

        stm.close();
    }

}
