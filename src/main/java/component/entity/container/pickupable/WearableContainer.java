package component.entity.container.pickupable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import component.entity.AbstractEntity;
import component.entity.container.AbstractContainer;
import component.entity.interfaces.IWearable;
import component.event.EventType;
import component.room.AbstractRoom;
import component.room.PlayableRoom;
import engine.GameManager;
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
    public StringBuilder wear() {
        StringBuilder outString = new StringBuilder();

        if (!worn) {
            worn = true;

            outString.append("Hai indossato: " + getName());
            outString.append(triggerEvent(EventType.UNWEAR));

            setActionPerformed(true);
        } else {
            outString.append("L'hai giá indossato.");
        }
        return outString;
    }

    @Override
    public StringBuilder unwear() {
        StringBuilder outString = new StringBuilder();

        if (worn) {
            worn = false;

            outString.append("Hai tolto: " + getName());
            outString.append(triggerEvent(EventType.UNWEAR));

            setActionPerformed(true);
        } else {
            outString.append("Non ce l'hai addosso.");
        }
        return outString;
    }

    @Override
    public StringBuilder pickup() {
        StringBuilder outString = new StringBuilder();

        pickedUp = true;
        GameManager.getInventory().add(this);

        // Check if it's an obj inside something and remove it from its list
        if (getParent() != null) {
            if (getParent() instanceof AbstractContainer) {
                AbstractContainer parentContainer = (AbstractContainer) getParent();
                parentContainer.getList().remove(this);
            } else if (getParent() instanceof PlayableRoom) {
                PlayableRoom room = (PlayableRoom) getParent();
                room.getObjects().remove(this);
            }
        }

        setParent(null);
        setClosestRoomParent(null);

        for (AbstractEntity obj : getAllObjectsInside(this)) {
            obj.setClosestRoomParent(null);
        }

        outString.append("Hai raccolto: " + getName());
        outString.append(triggerEvent(EventType.PICK_UP));

        setActionPerformed(true);
        return outString;
    }

    @Override
    public StringBuilder insert(AbstractEntity obj, List<AbstractEntity> inventory) {
        StringBuilder outString = new StringBuilder();

        if (maxSlots == 0) {
            maxSlots = 999;
        }

        if (getList().size() < maxSlots) {
            if (obj instanceof IWearable) {
                IWearable wearable = (IWearable) obj;

                if (wearable.isWorn()) {
                    outString.append("Devi prima toglierlo di dosso.");
                    return outString;
                }
            }

            obj.setClosestRoomParent((PlayableRoom) GameManager.getCurrentRoom());
            obj.setParent(this);
            inventory.remove(obj);

            this.add(obj);

            outString.append("Hai lasciato: " + obj.getName());
            outString.append(triggerEvent(EventType.INSERT));

            setActionPerformed(true);
        } else {
            outString.append("Non ci entra piú nulla. Libera spazio o tienilo nell'inventario!");
        }
        return outString;
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
