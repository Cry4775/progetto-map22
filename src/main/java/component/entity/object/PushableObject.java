package component.entity.object;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import component.entity.AbstractEntity;
import component.entity.interfaces.IPushable;
import component.entity.interfaces.IWearable;
import component.event.EventType;
import component.room.AbstractRoom;
import engine.OutputManager;
import engine.database.DBManager;

public class PushableObject extends AbstractEntity implements IPushable {

    private boolean pushed = false;

    public PushableObject(ResultSet resultSet) throws SQLException {
        super(resultSet);
        pushed = resultSet.getBoolean(6);
    }

    @Override
    public boolean isPushed() {
        return pushed;
    }

    @Override
    public void setPushed(boolean value) {
        pushed = value;
    }

    @Override
    public void push() {
        if (!pushed) {
            if (getRequiredWearedItemsToInteract() != null) {
                for (IWearable wearable : getRequiredWearedItemsToInteract()) {
                    if (!wearable.isWorn()) {
                        OutputManager.append(getFailedInteractionMessage());
                        return;
                    }
                }
            }

            pushed = true;

            OutputManager.append("Hai premuto: " + getName());
            triggerEvent((EventType.PUSH));

            setActionPerformed(true);
        } else {
            OutputManager.append("È stato già premuto.");
        }
    }

    @Override
    public void saveOnDB() throws SQLException {
        PreparedStatement stm = DBManager.getConnection().prepareStatement(
                "INSERT INTO SAVEDATA.PushableObject values (?, ?, ?, ?, ?, ?)");

        super.setKnownValuesOnStatement(stm);
        stm.setBoolean(6, pushed);
        stm.executeUpdate();

        saveExternalsOnDB();
    }

    public static void loadFromDB(List<AbstractRoom> allRooms) throws SQLException {
        PreparedStatement stm =
                DBManager.getConnection()
                        .prepareStatement("SELECT * FROM SAVEDATA.PushableObject");
        ResultSet resultSet = stm.executeQuery();

        while (resultSet.next()) {
            PushableObject obj = new PushableObject(resultSet);

            obj.loadLocation(resultSet, allRooms);
            obj.loadObjEvents();
        }

        stm.close();
    }

}
