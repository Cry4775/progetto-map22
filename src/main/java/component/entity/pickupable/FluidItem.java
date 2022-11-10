package component.entity.pickupable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import component.entity.AbstractEntity;
import component.entity.container.AbstractContainer;
import component.entity.interfaces.IFillable;
import component.entity.interfaces.IFluid;
import component.event.EventType;
import component.room.AbstractRoom;
import component.room.PlayableRoom;
import engine.GameManager;
import engine.OutputManager;
import engine.database.DBManager;

public class FluidItem extends BasicItem implements IFluid {

    public FluidItem(ResultSet resultSet) throws SQLException {
        super(resultSet);
    }

    @Override
    public void pickup() {
        if (isPickedUp()) {
            OutputManager.append("É giá nel tuo inventario.");
            return;
        }

        if (!canInteract())
            return;

        boolean filled = false;

        for (AbstractEntity invObject : GameManager.getInventory()) {
            if (invObject instanceof IFillable) {
                IFillable invFillable = (IFillable) invObject;

                filled = invFillable.fill(this);

                if (filled) {
                    if (getParent() instanceof AbstractContainer) {
                        AbstractContainer parentContainer = (AbstractContainer) getParent();
                        parentContainer.removeObject(this);
                    } else if (getParent() instanceof PlayableRoom) {
                        PlayableRoom room = (PlayableRoom) getParent();
                        room.removeObject(this);
                    }

                    setPickedUp(true);

                    OutputManager.append("Hai riempito: " + invObject.getName());
                    triggerEvent(EventType.PICK_UP);

                    setActionPerformed(true);
                    break;
                }
            }
        }

        if (!filled) {
            OutputManager.append("Non puoi prenderlo senza lo strumento adatto.");
        }
    }

    @Override
    public void delete() {
        if (getParent() instanceof IFillable) {
            IFillable fillable = (IFillable) getParent();

            fillable.setFilled(false);
            setParent(null);
            setClosestRoomParent(null);
        }
    }

    @Override
    public void saveOnDB() throws SQLException {
        PreparedStatement stm = DBManager.getConnection().prepareStatement(
                "INSERT INTO SAVEDATA.FluidItem values (?, ?, ?, ?, ?, ?)");

        setKnownValuesOnStatement(stm);
        stm.executeUpdate();

        saveExternalsOnDB();
    }

    public static void loadFromDB(List<AbstractRoom> allRooms) throws SQLException {
        PreparedStatement stm =
                DBManager.getConnection()
                        .prepareStatement("SELECT * FROM SAVEDATA.FluidItem");
        ResultSet resultSet = stm.executeQuery();

        while (resultSet.next()) {
            FluidItem obj = new FluidItem(resultSet);

            obj.loadLocation(resultSet, allRooms);
            obj.loadObjEvents();
        }

        stm.close();
    }

}
