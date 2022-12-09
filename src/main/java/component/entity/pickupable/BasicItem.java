package component.entity.pickupable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import component.entity.AbstractEntity;
import component.entity.container.AbstractContainer;
import component.entity.interfaces.IFluid;
import component.entity.interfaces.IPickupable;
import component.event.EventType;
import component.room.AbstractRoom;
import component.room.PlayableRoom;
import engine.Inventory;
import engine.MoveInformations.ActionState;
import engine.database.DBManager;
import gui.GUIManager;
import sound.SoundManager;
import sound.SoundManager.Channel;

public class BasicItem extends AbstractEntity implements IPickupable {

    private boolean pickedUp = false;

    public BasicItem(ResultSet resultSet) throws SQLException {
        super(resultSet);
        pickedUp = resultSet.getBoolean(6);
    }

    public boolean isPickedUp() {
        return pickedUp;
    }

    public void setPickedUp(boolean value) {
        pickedUp = value;
    }

    @Override
    public ActionState pickup(Inventory inventory) {
        if (!pickedUp) {
            if (!canInteract())
                return ActionState.NO_MOVE;

            // Check if it's an obj inside something and remove it from its list
            if (getParent() instanceof AbstractContainer) {
                AbstractContainer parentContainer = (AbstractContainer) getParent();
                parentContainer.removeObject(this);
            } else if (getParent() instanceof PlayableRoom) {
                PlayableRoom room = (PlayableRoom) getParent();
                room.removeObject(this);
            }

            inventory.addObject(this);
            SoundManager.playWav(SoundManager.PICKUP_SOUND_PATH, Channel.EFFECTS);
            pickedUp = true;

            GUIManager.appendOutput("Hai raccolto: " + getName());
            triggerEvent(EventType.PICK_UP);
            return ActionState.NORMAL_ACTION;
        } else {
            GUIManager.appendOutput("É giá nel tuo inventario.");
        }

        return ActionState.NO_MOVE;
    }

    @Override
    public void saveOnDB() throws SQLException {
        PreparedStatement stm = DBManager.getConnection().prepareStatement(
                "INSERT INTO SAVEDATA.BasicItem values (?, ?, ?, ?, ?, ?)");

        setKnownValuesOnStatement(stm);
        stm.executeUpdate();

        saveExternalsOnDB();
    }

    @Override
    public void setKnownValuesOnStatement(PreparedStatement stm) throws SQLException {
        super.setKnownValuesOnStatement(stm);
        if (pickedUp) {
            stm.setString(4, null);
            stm.setString(5, null);
        }

        stm.setBoolean(6, pickedUp);
    }

    public static void loadFromDB(List<AbstractRoom> allRooms, Inventory inventory) throws SQLException {
        PreparedStatement stm =
                DBManager.getConnection()
                        .prepareStatement("SELECT * FROM SAVEDATA.BasicItem");
        ResultSet resultSet = stm.executeQuery();

        while (resultSet.next()) {
            BasicItem obj = new BasicItem(resultSet);

            obj.loadLocation(resultSet, allRooms, inventory);
            obj.loadObjEvents();
        }

        stm.close();
    }

    @Override
    public void loadLocation(ResultSet resultSet, List<AbstractRoom> allRooms, Inventory inventory)
            throws SQLException {
        if (pickedUp) {
            if (!(this instanceof IFluid))
                inventory.addObject(this);
        } else {
            super.loadLocation(resultSet, allRooms, inventory);
        }
    }
}
