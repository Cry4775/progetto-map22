package component.entity.container;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import com.google.common.collect.Multimap;
import component.entity.AbstractEntity;
import component.entity.interfaces.IOpenable;
import component.event.EventType;
import component.room.AbstractRoom;
import engine.MoveInformations.ActionState;
import engine.database.DBManager;
import gui.GUIManager;
import utility.Triple;

public class ChestlikeContainer extends AbstractContainer implements IOpenable {

    private boolean open = false;
    private boolean locked = false;

    private AbstractEntity unlockedWithItem;
    private String unlockedWithItemId;

    public ChestlikeContainer(ResultSet resultSet) throws SQLException {
        super(resultSet);
        open = resultSet.getBoolean(6);
        locked = resultSet.getBoolean(7);
        unlockedWithItemId = resultSet.getString(8);
        setForFluids(resultSet.getBoolean(9));
    }

    @Override
    public boolean isOpen() {
        return open;
    }

    @Override
    public void setOpen(boolean open) {
        this.open = open;
    }

    @Override
    public boolean isLocked() {
        return locked;
    }

    @Override
    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    @Override
    public void lookAt() {
        IOpenable.super.lookAt();

        if (isOpen())
            GUIManager.appendOutput(getContentString());
    }

    @Override
    public String getUnlockedWithItemId() {
        return unlockedWithItemId;
    }

    @Override
    public ActionState open(AbstractEntity key) {
        if (locked) {
            if (!canInteract())
                return ActionState.NO_MOVE;

            if (unlockedWithItem.equals(key)) {
                locked = false;
                key.setMustDestroyFromInv(true);
                unlockedWithItem = null;
                unlockedWithItemId = null;
            } else {
                GUIManager.appendOutput(key == null ? "É chiusa a chiave." : "Non funziona.");
                triggerEvent(EventType.OPEN_LOCKED);
                return ActionState.NO_MOVE;
            }
        }

        if (!open) {
            if (!canInteract())
                return ActionState.NO_MOVE;

            open = true;
            GUIManager.appendOutput("Hai aperto: " + getName());
            GUIManager.appendOutput(getContentString());
            triggerEvent(EventType.OPEN_CONTAINER);
            return ActionState.NORMAL_ACTION;
        } else {
            GUIManager.appendOutput("É giá aperta. ");
            GUIManager.appendOutput(getContentString());
        }

        return ActionState.NO_MOVE;
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
    public void saveOnDB() throws SQLException {
        PreparedStatement stm = DBManager.getConnection().prepareStatement(
                "INSERT INTO SAVEDATA.ChestlikeContainer values (?, ?, ?, ?, ?, ?, ?, ?, ?)");

        setKnownValuesOnStatement(stm);
        stm.setBoolean(6, open);
        stm.setBoolean(7, locked);
        stm.setString(8, unlockedWithItemId);
        stm.setBoolean(9, isForFluids());
        stm.executeUpdate();

        saveExternalsOnDB();
    }

    public static void loadFromDB(List<AbstractRoom> allRooms,
            List<Triple<AbstractEntity, String, String>> pendingList) throws SQLException {
        PreparedStatement stm =
                DBManager.getConnection()
                        .prepareStatement("SELECT * FROM SAVEDATA.ChestlikeContainer");
        ResultSet resultSet = stm.executeQuery();

        while (resultSet.next()) {
            ChestlikeContainer obj = new ChestlikeContainer(resultSet);

            Triple<AbstractEntity, String, String> pending =
                    obj.loadRoomLocation(resultSet, allRooms);
            obj.loadObjEvents();

            if (pending != null)
                pendingList.add(pending);
        }

        stm.close();
    }
}
