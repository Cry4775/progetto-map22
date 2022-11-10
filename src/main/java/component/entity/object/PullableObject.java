package component.entity.object;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import component.entity.AbstractEntity;
import component.entity.interfaces.IPullable;
import component.event.EventType;
import component.room.AbstractRoom;
import engine.OutputManager;
import engine.database.DBManager;

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
    public void pull() {
        if (!pulled) {
            if (!canInteract())
                return;

            pulled = true;

            OutputManager.append("Hai tirato: " + getName());
            triggerEvent((EventType.PULL));

            setActionPerformed(true);
        } else {
            OutputManager.append("È stato già tirato.");
        }
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

    public static void loadFromDB(List<AbstractRoom> allRooms) throws SQLException {
        PreparedStatement stm =
                DBManager.getConnection()
                        .prepareStatement("SELECT * FROM SAVEDATA.PullableObject");
        ResultSet resultSet = stm.executeQuery();

        while (resultSet.next()) {
            PullableObject obj = new PullableObject(resultSet);

            obj.loadLocation(resultSet, allRooms);
            obj.loadObjEvents();
        }

        stm.close();
    }

}
