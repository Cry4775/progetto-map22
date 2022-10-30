package component.entity.object;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import com.google.common.collect.Multimap;
import component.entity.AbstractEntity;
import component.entity.container.AbstractContainer;
import component.entity.interfaces.IFluid;
import component.entity.interfaces.IWearable;
import component.event.EventType;
import component.event.ObjectEvent;
import component.room.AbstractRoom;
import component.room.PlayableRoom;

public class FireObject extends AbstractEntity {

    private boolean lit = false;

    public boolean isLit() {
        return lit;
    }

    public void setLit(boolean lit) {
        this.lit = lit;
    }

    public StringBuilder extinguish() {
        StringBuilder outString = new StringBuilder();

        if (lit) {

            if (getRequiredWearedItemsToInteract() != null) {
                for (IWearable wearable : getRequiredWearedItemsToInteract()) {
                    if (!wearable.isWorn()) {
                        outString.append(getFailedInteractionMessage()); // TODO check per null e
                                                                         // default message
                        return outString;
                    }
                }
            }

            lit = false;

            outString.append(processEvent(EventType.EXTINGUISH));

            setActionPerformed(true);
        } else {
            outString.append("Non ci sono piú fiamme.");
        }

        return outString;
    }

    public StringBuilder extinguish(IFluid liquid) {
        StringBuilder outString = new StringBuilder();

        if (lit) {

            if (getRequiredWearedItemsToInteract() != null) {
                for (IWearable wearable : getRequiredWearedItemsToInteract()) {
                    if (!wearable.isWorn()) {
                        outString.append(getFailedInteractionMessage()); // TODO check per null e
                                                                         // default message
                        return outString;
                    }
                }
            }

            lit = false;
            liquid.delete();

            outString.append(processEvent(EventType.EXTINGUISH));
            setActionPerformed(true);
        } else {
            outString.append("Non ci sono piú fiamme.");
        }

        return outString;
    }

    @Override
    public void processReferences(Multimap<String, AbstractEntity> objects,
            List<AbstractRoom> rooms) {
        processRoomParent(rooms);
        processEventReferences(objects, rooms);
    }

    @Override
    public void saveOnDB(Connection connection) throws SQLException {
        PreparedStatement stm = connection.prepareStatement(
                "INSERT INTO SAVEDATA.FireObject values (?, ?, ?, ?)");
        PreparedStatement evtStm = connection.prepareStatement(
                "INSERT INTO SAVEDATA.ObjectEvent values (?, ?, ?)");

        stm.setString(1, getId());

        if (getParent() instanceof PlayableRoom) {
            stm.setString(2, getParent().getId());
            stm.setString(3, "null");
        } else if (getParent() instanceof AbstractContainer) {
            stm.setString(2, "null");
            stm.setString(3, getParent().getId());
        }

        stm.setBoolean(4, lit);
        stm.executeUpdate();

        for (ObjectEvent evt : getEvents()) {
            evtStm.setString(1, getId());
            evtStm.setString(2, evt.getEventType().toString());
            evtStm.setString(3, evt.getText());
            evtStm.executeUpdate();
        }
    }

}
