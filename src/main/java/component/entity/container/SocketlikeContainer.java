package component.entity.container;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import com.google.common.collect.Multimap;
import component.entity.AbstractEntity;
import component.entity.interfaces.IWearable;
import component.event.EventType;
import component.room.AbstractRoom;
import engine.database.DBManager;
import utility.Triple;

public class SocketlikeContainer extends AbstractContainer {

    public SocketlikeContainer(ResultSet resultSet) throws SQLException {
        super(resultSet);
        itemInside = resultSet.getBoolean(6);
        eligibleItemId = resultSet.getString(7);
        setForFluids(resultSet.getBoolean(8));
    }

    private boolean itemInside = false;

    private AbstractEntity eligibleItem;
    private String eligibleItemId;

    public boolean isItemInside() {
        return itemInside;
    }

    public void setItemInside(boolean itemInside) {
        this.itemInside = itemInside;
    }

    public AbstractEntity getEligibleItem() {
        return eligibleItem;
    }

    public void setEligibleItem(AbstractEntity eligibleItem) {
        this.eligibleItem = eligibleItem;
    }

    public String getEligibleItemId() {
        return eligibleItemId;
    }

    public void setEligibleItemId(String eligibleItemId) {
        this.eligibleItemId = eligibleItemId;
    }

    @Override
    public StringBuilder insert(AbstractEntity obj, List<AbstractEntity> inventory) {
        StringBuilder outString = new StringBuilder();

        if (!itemInside) {
            if (eligibleItem.equals(obj)) {
                if (obj instanceof IWearable) {
                    IWearable wearable = (IWearable) obj;

                    if (wearable.isWorn()) {
                        outString.append("Devi prima toglierlo di dosso.");
                        return outString;
                    }
                }

                if (obj.getParent() instanceof AbstractContainer) {
                    AbstractContainer container = (AbstractContainer) obj.getParent();

                    container.remove(obj);
                }

                itemInside = true;
                obj.setParent(this);
                inventory.remove(obj);

                this.add(obj);

                outString.append("Hai inserito: " + obj.getName());
                outString.append(processEvent(EventType.INSERT));
            } else {
                outString.append("Non puoi inserirci questo oggetto.");
            }
        } else {
            outString.append("Non puoi inserirci altri oggetti.");
        }
        return outString;
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
    public void saveOnDB(Connection connection) throws SQLException {
        PreparedStatement stm = connection.prepareStatement(
                "INSERT INTO SAVEDATA.SocketlikeContainer values (?, ?, ?, ?, ?, ?, ?, ?)");

        setValuesOnStatement(stm);
        stm.setBoolean(6, itemInside);
        stm.setString(7, eligibleItemId);
        stm.setBoolean(8, isForFluids());

        stm.executeUpdate();

        saveExternalsOnDB(connection);
    }

    public static void loadFromDB(List<AbstractRoom> allRooms,
            List<Triple<AbstractEntity, String, String>> pendingList) throws SQLException {
        PreparedStatement stm =
                DBManager.getConnection()
                        .prepareStatement("SELECT * FROM SAVEDATA.SocketlikeContainer");
        ResultSet resultSet = stm.executeQuery();

        while (resultSet.next()) {
            SocketlikeContainer obj = new SocketlikeContainer(resultSet);

            Triple<AbstractEntity, String, String> pending = obj.loadLocation(resultSet, allRooms);

            if (pending != null)
                pendingList.add(pending);
        }

        stm.close();
    }

}
