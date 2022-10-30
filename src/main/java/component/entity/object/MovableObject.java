package component.entity.object;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import com.google.common.collect.Multimap;
import component.entity.AbstractEntity;
import component.entity.container.AbstractContainer;
import component.entity.interfaces.IMovable;
import component.entity.interfaces.IWearable;
import component.event.EventType;
import component.event.ObjectEvent;
import component.room.AbstractRoom;
import component.room.PlayableRoom;

public class MovableObject extends AbstractEntity implements IMovable {

    private boolean moved = false;

    @Override
    public boolean isMoved() {
        return moved;
    }

    @Override
    public void setMoved(boolean value) {
        moved = value;
    }

    @Override
    public StringBuilder move() {
        StringBuilder outString = new StringBuilder();

        if (!moved) {

            if (getRequiredWearedItemsToInteract() != null) {
                for (IWearable wearable : getRequiredWearedItemsToInteract()) {
                    if (!wearable.isWorn()) {
                        outString.append(getFailedInteractionMessage());
                        return outString;
                    }
                }
            }

            moved = true;
            outString.append("Hai spostato: " + getName());
            outString.append(processEvent(EventType.MOVE));

            setActionPerformed(true);
        } else {
            outString.append("È stato già spostato.");
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
                "INSERT INTO SAVEDATA.MovableObject values (?, ?, ?, ?)");
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

        stm.setBoolean(4, moved);
        stm.executeUpdate();

        for (ObjectEvent evt : getEvents()) {
            evtStm.setString(1, getId());
            evtStm.setString(2, evt.getEventType().toString());
            evtStm.setString(3, evt.getText());
            evtStm.executeUpdate();
        }
    }

}
