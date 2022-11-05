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
import engine.database.DBManager;

public class FluidItem extends BasicItem implements IFluid {

    public FluidItem(ResultSet resultSet) throws SQLException {
        super(resultSet);
    }

    @Override
    public StringBuilder pickup() {
        StringBuilder outString = new StringBuilder();

        if (isPickedUp()) {
            outString.append("É giá nel tuo inventario.");
            return outString;
        }

        boolean canProceed = false;

        for (AbstractEntity invObject : GameManager.getInventory()) {
            if (invObject instanceof IFillable) {
                IFillable invFillable = (IFillable) invObject;

                canProceed = invFillable.fill(this);

                if (canProceed) {
                    outString.append("Hai riempito: " + invObject.getName());
                    outString.append(processEvent(EventType.PICK_UP));

                    setActionPerformed(true);

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

                    setPickedUp(true);
                    break;
                }
            }
        }

        if (!canProceed) {
            outString.append("Non puoi prenderlo senza lo strumento adatto.");
        }

        return outString;
    }

    @Override
    public void delete() {
        if (getParent() instanceof IFillable) {
            IFillable container = (IFillable) getParent();

            container.setFilled(false);
            setParent(null);
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
