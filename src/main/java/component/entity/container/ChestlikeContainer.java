/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package component.entity.container;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import com.google.common.collect.Multimap;
import component.entity.AbstractEntity;
import component.entity.interfaces.IOpenable;
import component.entity.pickupable.BasicItem;
import component.event.EventType;
import component.room.AbstractRoom;
import engine.database.DBManager;
import utility.Triple;

public class ChestlikeContainer extends AbstractContainer implements IOpenable {
    public ChestlikeContainer(ResultSet resultSet) throws SQLException {
        super(resultSet);
        open = resultSet.getBoolean(6);
        locked = resultSet.getBoolean(7);
        unlockedWithItemId = resultSet.getString(8);
        setForFluids(resultSet.getBoolean(9));
    }

    private boolean open = false;
    private boolean locked = false;

    private AbstractEntity unlockedWithItem;
    private String unlockedWithItemId;

    @Override
    public boolean isOpen() {
        return open;
    }

    @Override
    public void setOpen(boolean open) {
        this.open = open;
    }

    @Override
    public StringBuilder open(AbstractEntity key) {
        StringBuilder outString = new StringBuilder();

        if (locked) {
            if (unlockedWithItem.equals(key)) {
                locked = false;
                key.setMustDestroyFromInv(true);
            } else {
                outString.append(key == null ? "É chiusa a chiave." : "Non funziona.");
                outString.append(processEvent(EventType.OPEN_LOCKED));
                return outString;
            }
        }

        if (!open) {
            open = true;
            outString.append("Hai aperto: " + getName());
            outString.append(getContentString());
            outString.append(processEvent(EventType.OPEN_CONTAINER));

            setActionPerformed(true);
        } else {
            outString.append("É giá aperta. ");
            outString.append(getContentString());
        }
        return outString;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    @Override
    public String getUnlockedWithItemId() {
        return unlockedWithItemId;
    }

    public AbstractEntity getUnlockedWithItem() {
        return unlockedWithItem;
    }

    public void setUnlockedWithItem(BasicItem unlockedWithItem) {
        this.unlockedWithItem = unlockedWithItem;
    }

    @Override
    public void processReferences(Multimap<String, AbstractEntity> objects,
            List<AbstractRoom> rooms) {
        super.processReferences(objects, rooms);

        if (unlockedWithItemId != null) {
            if (!objects.containsKey(unlockedWithItemId)) {
                throw new Error(
                        "Couldn't find the requested \"unlockedWithItem\" ID on " + getName()
                                + " (" + getId()
                                + "). Check the JSON file for correct object IDs.");
            }

            for (AbstractEntity reqItem : objects.get(unlockedWithItemId)) {
                unlockedWithItem = reqItem;
            }
        }
    }

    @Override
    public void saveOnDB(Connection connection) throws SQLException {
        PreparedStatement stm = connection.prepareStatement(
                "INSERT INTO SAVEDATA.ChestlikeContainer values (?, ?, ?, ?, ?, ?, ?, ?, ?)");

        setValuesOnStatement(stm);
        stm.setBoolean(6, open);
        stm.setBoolean(7, locked);
        stm.setString(8, unlockedWithItemId);
        stm.setBoolean(9, isForFluids());
        stm.executeUpdate();

        saveExternalsOnDB(connection);
    }

    public static void loadFromDB(List<AbstractRoom> allRooms,
            List<Triple<AbstractEntity, String, String>> pendingList) throws SQLException {
        PreparedStatement stm =
                DBManager.getConnection()
                        .prepareStatement("SELECT * FROM SAVEDATA.ChestlikeContainer");
        ResultSet resultSet = stm.executeQuery();

        while (resultSet.next()) {
            ChestlikeContainer obj = new ChestlikeContainer(resultSet);

            Triple<AbstractEntity, String, String> pending = obj.loadLocation(resultSet, allRooms);

            if (pending != null)
                pendingList.add(pending);
        }

        stm.close();
    }
}
