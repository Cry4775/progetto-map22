package component.entity.container;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import com.google.common.collect.Multimap;
import component.entity.AbstractEntity;
import component.entity.interfaces.IPickupable;
import component.entity.interfaces.IWearable;
import component.event.EventType;
import component.room.AbstractRoom;
import component.room.PlayableRoom;
import engine.GameManager;
import engine.database.DBManager;
import gui.GUIManager;
import utility.Triple;

public class SocketlikeContainer extends AbstractContainer {

    private boolean itemInside = false;

    private AbstractEntity eligibleItem;
    private String eligibleItemId;

    public SocketlikeContainer(ResultSet resultSet) throws SQLException {
        super(resultSet);
        itemInside = resultSet.getBoolean(6);
        eligibleItemId = resultSet.getString(7);
        setForFluids(resultSet.getBoolean(8));
    }

    @Override
    public boolean insert(AbstractEntity obj) {
        if (!itemInside) {
            if (!canInteract())
                return false;

            if (eligibleItem.equals(obj)) {
                if (obj instanceof IWearable) {
                    IWearable wearable = (IWearable) obj;

                    if (wearable.isWorn()) {
                        GUIManager.appendOutput("Devi prima toglierlo di dosso.");
                        return false;
                    }
                }

                if (obj.getParent() instanceof AbstractContainer) {
                    AbstractContainer container = (AbstractContainer) obj.getParent();

                    container.removeObject(obj);
                }

                itemInside = true;
                obj.setClosestRoomParent((PlayableRoom) GameManager.getCurrentRoom());
                obj.setParent(this);
                GameManager.getInventory().remove(obj);
                ((IPickupable) obj).setPickedUp(false);

                this.add(obj);

                GUIManager.appendOutput("Hai inserito: " + obj.getName());
                triggerEvent(EventType.INSERT);
                return true;
            } else {
                GUIManager.appendOutput("Non puoi inserirci questo oggetto.");
            }
        } else {
            GUIManager.appendOutput("Non puoi inserirci altri oggetti.");
        }

        return false;
    }

    @Override
    public void processReferences(Multimap<String, AbstractEntity> objects,
            List<AbstractRoom> rooms) {
        super.processReferences(objects, rooms);

        if (eligibleItemId != null) {
            if (!objects.containsKey(eligibleItemId)) {
                throw new Error(
                        "Couldn't find the requested \"eligibleItem\" ID on " + getName()
                                + " (" + getId()
                                + "). Check the JSON file for correct object IDs.");
            }

            for (AbstractEntity reqItem : objects.get(eligibleItemId)) {
                eligibleItem = reqItem;
            }
        }
    }

    @Override
    public void saveOnDB() throws SQLException {
        PreparedStatement stm = DBManager.getConnection().prepareStatement(
                "INSERT INTO SAVEDATA.SocketlikeContainer values (?, ?, ?, ?, ?, ?, ?, ?)");

        setKnownValuesOnStatement(stm);
        stm.setBoolean(6, itemInside);
        stm.setString(7, eligibleItemId);
        stm.setBoolean(8, isForFluids());

        stm.executeUpdate();

        saveExternalsOnDB();
    }

    public static void loadFromDB(List<AbstractRoom> allRooms,
            List<Triple<AbstractEntity, String, String>> pendingList) throws SQLException {
        PreparedStatement stm =
                DBManager.getConnection()
                        .prepareStatement("SELECT * FROM SAVEDATA.SocketlikeContainer");
        ResultSet resultSet = stm.executeQuery();

        while (resultSet.next()) {
            SocketlikeContainer obj = new SocketlikeContainer(resultSet);

            Triple<AbstractEntity, String, String> pending =
                    obj.loadRoomLocation(resultSet, allRooms);
            obj.loadObjEvents();

            if (pending != null)
                pendingList.add(pending);
        }

        stm.close();
    }

}
