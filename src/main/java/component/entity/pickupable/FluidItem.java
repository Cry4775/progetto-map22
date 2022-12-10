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
import engine.Inventory;
import engine.MoveInformations.ActionState;
import engine.database.DBManager;
import gui.GUIManager;

public class FluidItem extends BasicItem implements IFluid {

    public FluidItem(ResultSet resultSet) throws SQLException {
        super(resultSet);
    }

    @Override
    public ActionState pickup(Inventory inventory) {
        if (isPickedUp()) {
            GUIManager.appendOutput("É giá nel tuo inventario.");
            return ActionState.NO_MOVE;
        }

        if (!canInteract())
            return ActionState.NO_MOVE;

        boolean filled = false;

        for (AbstractEntity invObject : inventory.getObjects()) {
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

                    GUIManager.appendOutput("Hai riempito: " + invObject.getName());
                    triggerEvent(EventType.PICK_UP);
                    return ActionState.NORMAL_ACTION;
                }
            }
        }

        GUIManager.appendOutput("Non puoi prenderlo senza lo strumento adatto.");
        return ActionState.NO_MOVE;
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

    /**
     * Loads all fluid items from DB.
     * 
     * @param allRooms all the possible rooms list.
     * @param inventory the inventory reference.
     * @throws SQLException
     */
    public static void loadFromDB(List<AbstractRoom> allRooms, Inventory inventory) throws SQLException {
        PreparedStatement stm =
                DBManager.getConnection()
                        .prepareStatement("SELECT * FROM SAVEDATA.FluidItem");
        ResultSet resultSet = stm.executeQuery();

        while (resultSet.next()) {
            FluidItem obj = new FluidItem(resultSet);

            obj.loadLocation(resultSet, allRooms, inventory);
            obj.loadObjEvents();
        }

        stm.close();
    }

}
