package component.entity.pickupable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import component.entity.AbstractEntity;
import component.entity.container.AbstractContainer;
import component.entity.interfaces.IFillable;
import component.entity.interfaces.IFluid;
import component.event.EventType;
import component.room.PlayableRoom;

public class FluidItem extends BasicItem implements IFluid {

    public FluidItem(ResultSet resultSet) throws SQLException {
        super(resultSet);
    }

    @Override
    public StringBuilder pickup(List<AbstractEntity> inventory) {
        StringBuilder outString = new StringBuilder();

        if (isPickedUp()) {
            outString.append("É giá nel tuo inventario.");
            return outString;
        }

        boolean canProceed = false;

        for (AbstractEntity invObject : inventory) {
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
                            setParent(null);
                        } else if (getParent() instanceof PlayableRoom) {
                            PlayableRoom room = (PlayableRoom) getParent();
                            room.getObjects().remove(this);
                        }
                    }

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
    public void saveOnDB(Connection connection) throws SQLException {
        PreparedStatement stm = connection.prepareStatement(
                "INSERT INTO SAVEDATA.FluidItem values (?, ?, ?, ?, ?, ?)");

        stm.setString(1, getId());
        stm.setString(2, getName());
        stm.setString(3, getDescription());

        if (getParent() instanceof PlayableRoom) {
            stm.setString(4, getClosestRoomParent().getId());
            stm.setString(5, "null");
        } else if (getParent() instanceof AbstractContainer) {
            stm.setString(4, "null");
            stm.setString(5, getParent().getId());
        }

        stm.setBoolean(6, isPickedUp());
        stm.executeUpdate();

        saveExternalsOnDB(connection);
    }

}
