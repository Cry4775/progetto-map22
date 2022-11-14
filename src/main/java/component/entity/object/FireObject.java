package component.entity.object;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import component.entity.AbstractEntity;
import component.entity.interfaces.IFluid;
import component.event.EventType;
import component.room.AbstractRoom;
import engine.Inventory;
import engine.database.DBManager;
import gui.GUIManager;

public class FireObject extends AbstractEntity {

    private boolean lit = false;

    public FireObject(ResultSet resultSet) throws SQLException {
        super(resultSet);
        lit = resultSet.getBoolean(6);
    }

    public boolean isLit() {
        return lit;
    }

    public void setLit(boolean lit) {
        this.lit = lit;
    }

    public boolean extinguish(IFluid liquid) {
        if (lit) {
            if (!canInteract())
                return false;

            lit = false;

            if (liquid != null)
                liquid.delete();

            triggerEvent((EventType.EXTINGUISH));
            return true;
        } else {
            GUIManager.appendOutput("Non ci sono piú fiamme.");
        }

        return false;
    }

    @Override
    public void saveOnDB() throws SQLException {
        PreparedStatement stm = DBManager.getConnection().prepareStatement(
                "INSERT INTO SAVEDATA.FireObject values (?, ?, ?, ?, ?, ?)");

        super.setKnownValuesOnStatement(stm);
        stm.setBoolean(6, lit);
        stm.executeUpdate();

        saveExternalsOnDB();
    }

    public static void loadFromDB(List<AbstractRoom> allRooms, Inventory inventory) throws SQLException {
        PreparedStatement stm =
                DBManager.getConnection()
                        .prepareStatement("SELECT * FROM SAVEDATA.FireObject");
        ResultSet resultSet = stm.executeQuery();

        while (resultSet.next()) {
            FireObject obj = new FireObject(resultSet);

            obj.loadLocation(resultSet, allRooms, inventory);
            obj.loadObjEvents();
        }

        stm.close();
    }

}
