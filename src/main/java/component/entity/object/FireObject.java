package component.entity.object;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import component.entity.AbstractEntity;
import component.entity.interfaces.IFluid;
import component.entity.interfaces.IWearable;
import component.event.EventType;
import component.room.AbstractRoom;
import engine.OutputManager;
import engine.database.DBManager;

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

    public void extinguish() {
        if (lit) {
            if (getRequiredWearedItemsToInteract() != null) {
                for (IWearable wearable : getRequiredWearedItemsToInteract()) {
                    if (!wearable.isWorn()) {
                        OutputManager.append(getFailedInteractionMessage());
                    }
                }
            }

            lit = false;

            triggerEvent((EventType.EXTINGUISH));

            setActionPerformed(true);
        } else {
            OutputManager.append("Non ci sono piú fiamme.");
        }
    }

    public void extinguish(IFluid liquid) {
        if (lit) {
            if (getRequiredWearedItemsToInteract() != null) {
                for (IWearable wearable : getRequiredWearedItemsToInteract()) {
                    if (!wearable.isWorn()) {
                        OutputManager.append(getFailedInteractionMessage());
                        return;
                    }
                }
            }

            lit = false;
            liquid.delete();

            triggerEvent((EventType.EXTINGUISH));
            setActionPerformed(true);
        } else {
            OutputManager.append("Non ci sono piú fiamme.");
        }
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

    public static void loadFromDB(List<AbstractRoom> allRooms) throws SQLException {
        PreparedStatement stm =
                DBManager.getConnection()
                        .prepareStatement("SELECT * FROM SAVEDATA.FireObject");
        ResultSet resultSet = stm.executeQuery();

        while (resultSet.next()) {
            FireObject obj = new FireObject(resultSet);

            obj.loadLocation(resultSet, allRooms);
            obj.loadObjEvents();
        }

        stm.close();
    }

}
