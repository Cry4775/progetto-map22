package component.entity.container.pickupable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import component.entity.AbstractEntity;
import component.entity.container.AbstractContainer;
import component.entity.interfaces.IPickupable;
import component.entity.interfaces.IWearable;
import component.event.EventType;
import component.room.AbstractRoom;
import component.room.PlayableRoom;
import engine.GameManager;
import engine.OutputManager;
import engine.database.DBManager;
import utility.Triple;

public class WearableContainer extends AbstractContainer implements IWearable {

    private boolean worn = false;
    private boolean pickedUp = false;
    private int maxSlots;

    public WearableContainer(ResultSet resultSet) throws SQLException {
        super(resultSet);
        pickedUp = resultSet.getBoolean(6);
        worn = resultSet.getBoolean(7);
        maxSlots = resultSet.getInt(8);
        setForFluids(resultSet.getBoolean(9));
    }

    @Override
    public boolean isWorn() {
        return worn;
    }

    @Override
    public void setWorn(boolean value) {
        this.worn = value;
    }

    @Override
    public boolean isPickedUp() {
        return pickedUp;
    }

    @Override
    public void setPickedUp(boolean value) {
        pickedUp = value;
    }

    @Override
    public void wear() {
        if (!worn) {
            if (!canInteract())
                return;

            worn = true;

            OutputManager.append("Hai indossato: " + getName());
            triggerEvent((EventType.UNWEAR));

            setActionPerformed(true);
        } else {
            OutputManager.append("L'hai giá indossato.");
        }
    }

    @Override
    public void unwear() {
        if (worn) {
            worn = false;

            OutputManager.append("Hai tolto: " + getName());
            triggerEvent((EventType.UNWEAR));

            setActionPerformed(true);
        } else {
            OutputManager.append("Non ce l'hai addosso.");
        }
    }

    @Override
    public void pickup() {
        if (!canInteract())
            return;

        pickedUp = true;
        GameManager.getInventory().add(this);

        // Check if it's an obj inside something and remove it from its list
        if (getParent() != null) {
            if (getParent() instanceof AbstractContainer) {
                AbstractContainer parentContainer = (AbstractContainer) getParent();
                parentContainer.removeObject(this);
            } else if (getParent() instanceof PlayableRoom) {
                PlayableRoom room = (PlayableRoom) getParent();
                room.removeObject(this);
            }
        }

        for (AbstractEntity obj : getAllObjectsInside(this)) {
            obj.setClosestRoomParent(null);
        }

        OutputManager.append("Hai raccolto: " + getName());
        triggerEvent((EventType.PICK_UP));

        setActionPerformed(true);
    }

    @Override
    public void insert(AbstractEntity obj) {
        if (maxSlots == 0) {
            maxSlots = 999;
        }

        if (getList().size() < maxSlots) {
            if (obj instanceof IWearable) {
                IWearable wearable = (IWearable) obj;

                if (wearable.isWorn()) {
                    OutputManager.append("Devi prima toglierlo di dosso.");
                    return;
                }
            }

            obj.setClosestRoomParent((PlayableRoom) GameManager.getCurrentRoom());
            obj.setParent(this);
            GameManager.getInventory().remove(obj);
            ((IPickupable) obj).setPickedUp(false);

            this.add(obj);

            OutputManager.append("Hai lasciato: " + obj.getName());
            triggerEvent((EventType.INSERT));

            setActionPerformed(true);
        } else {
            OutputManager.append("Non ci entra piú nulla. Libera spazio o tienilo nell'inventario!");
        }
    }

    @Override
    public void saveOnDB() throws SQLException {
        PreparedStatement stm = DBManager.getConnection().prepareStatement(
                "INSERT INTO SAVEDATA.WearableContainer values (?, ?, ?, ?, ?, ?, ?, ?, ?)");

        setKnownValuesOnStatement(stm);
        stm.setBoolean(7, worn);
        stm.setInt(8, maxSlots);
        stm.setBoolean(9, isForFluids());
        stm.executeUpdate();

        saveExternalsOnDB();
    }

    @Override
    public void setKnownValuesOnStatement(PreparedStatement stm) throws SQLException {
        super.setKnownValuesOnStatement(stm);
        if (pickedUp) {
            stm.setString(4, null);
            stm.setString(5, null);
        }

        stm.setBoolean(6, pickedUp);
    }

    public static void loadFromDB(List<AbstractRoom> allRooms,
            List<Triple<AbstractEntity, String, String>> pendingList) throws SQLException {
        PreparedStatement stm =
                DBManager.getConnection()
                        .prepareStatement("SELECT * FROM SAVEDATA.WearableContainer");
        ResultSet resultSet = stm.executeQuery();

        while (resultSet.next()) {
            WearableContainer obj = new WearableContainer(resultSet);

            if (obj.isPickedUp()) {
                GameManager.getInventory().add(obj);
            } else {
                Triple<AbstractEntity, String, String> pending =
                        obj.loadRoomLocation(resultSet, allRooms);
                obj.loadObjEvents();

                if (pending != null)
                    pendingList.add(pending);
            }
        }

        stm.close();
    }

}
