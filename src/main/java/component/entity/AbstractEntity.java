package component.entity;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.google.common.collect.Multimap;
import component.GameComponent;
import component.entity.container.AbstractContainer;
import component.entity.interfaces.IWearable;
import component.event.EventType;
import component.event.ObjectEvent;
import component.room.AbstractRoom;
import component.room.MutableRoom;
import component.room.PlayableRoom;
import component.room.PlayableRoom.Mode;
import component.room.Rooms;
import engine.Inventory;
import engine.database.DBManager;
import gui.GUIManager;

public abstract class AbstractEntity extends GameComponent {

    protected static int DB_ROOM_ID_COLUMN = 4;
    protected static int DB_CONTAINER_ID_COLUMN = 5;

    private Set<String> alias = new HashSet<>();

    private boolean destroyFromInventory = false;

    private GameComponent parent;

    private PlayableRoom closestRoomParent;
    private String closestRoomParentId;

    private List<ObjectEvent> events = new ArrayList<>();

    private List<String> requiredWearedItemsIdToInteract = new ArrayList<>();

    private List<IWearable> requiredWearedItemsToInteract = new ArrayList<>();

    private String failedInteractionMessage;

    protected AbstractEntity(ResultSet resultSet) throws SQLException {
        super(resultSet);

        PreparedStatement stm = DBManager.getConnection().prepareStatement(
                "SELECT * FROM SAVEDATA.RequiredWearedItem WHERE ID = " + getId());
        ResultSet rs = stm.executeQuery();

        while (rs.next()) {
            requiredWearedItemsIdToInteract.add(rs.getString(2));

            if (failedInteractionMessage == null) {
                failedInteractionMessage = rs.getString(3);
            }
        }

        stm.close();

        stm = DBManager.getConnection().prepareStatement(
                "SELECT * FROM SAVEDATA.Alias WHERE ID = " + getId());
        rs = stm.executeQuery();

        while (rs.next()) {
            addAlias(rs.getString(2));
        }

        stm.close();
    }

    public void setClosestRoomParentId(String closestRoomParentId) {
        this.closestRoomParentId = closestRoomParentId;
    }

    protected String getFailedInteractionMessage() {
        return failedInteractionMessage;
    }

    public Set<String> getAlias() {
        return alias;
    }

    public void addAlias(String alias) {
        this.alias.add(alias);
    }

    public PlayableRoom getClosestRoomParent() {
        return closestRoomParent;
    }

    public void setClosestRoomParent(PlayableRoom closestRoomParent) {
        this.closestRoomParent = closestRoomParent;
    }

    public List<ObjectEvent> getEvents() {
        return events;
    }

    public boolean mustDestroyFromInventory() {
        return destroyFromInventory;
    }

    public void setDestroyFromInventory(boolean destroyFromInventory) {
        this.destroyFromInventory = destroyFromInventory;
    }

    public GameComponent getParent() {
        return parent;
    }

    public void setParent(GameComponent parent) {
        this.parent = parent;
    }

    protected List<String> getRequiredWearedItemsIdToInteract() {
        return requiredWearedItemsIdToInteract;
    }

    protected List<IWearable> getRequiredWearedItemsToInteract() {
        return requiredWearedItemsToInteract;
    }

    protected void setRequiredWearedItemsToInteract(List<IWearable> requiredWearedItemsToInteract) {
        this.requiredWearedItemsToInteract = requiredWearedItemsToInteract;
    }

    public void lookAt() {
        GUIManager.appendOutput(getDescription());

        if (GUIManager.isOutputEmpty()) {
            GUIManager.appendOutput("Nulla di particolare.");
        }

        triggerEvent(EventType.LOOK_AT);
    }

    protected void triggerEvent(EventType type) {
        ObjectEvent evt = ObjectEvent.getEvent(events, type);

        if (evt != null)
            evt.trigger(this);
    }

    protected boolean canInteract() {
        if (getRequiredWearedItemsToInteract() != null) {
            for (IWearable wearable : getRequiredWearedItemsToInteract()) {
                if (!wearable.isWorn()) {
                    GUIManager.appendOutput(getFailedInteractionMessage());
                    return false;
                }
            }
        }

        return true;
    }

    public void processReferences(Multimap<String, AbstractEntity> objects,
            List<AbstractRoom> rooms) {
        processRoomParent(rooms);
        processEventReferences(objects, rooms);
    }

    private void processRoomParent(List<AbstractRoom> rooms) {

        for (PlayableRoom room : Rooms.listCheckedRooms(PlayableRoom.class, rooms)) {
            if (room.getObjects(Mode.INCLUDE_EVERYTHING).contains(this)) {
                if (parent == null)
                    parent = room;

                for (PlayableRoom _room : Rooms.listCheckedRooms(PlayableRoom.class, Rooms.getAllRooms(room))) {
                    if (_room.getObjects(Mode.UNPACK_CONTAINERS).contains(this)) {
                        closestRoomParent = _room;
                    }
                }
            }
        }
    }

    private void processEventReferences(Multimap<String, AbstractEntity> objects,
            List<AbstractRoom> rooms) {
        if (events != null) {
            for (ObjectEvent evt : events) {
                boolean targetRoomDone = false;
                boolean parentRoomDone = false;
                boolean teleportRoomDone = false;

                if (evt.isUpdatingParentRoom()) {
                    if (closestRoomParent instanceof MutableRoom) {
                        evt.setParentRoom((MutableRoom) closestRoomParent);
                        parentRoomDone = true;
                    } else {
                        throw new Error("Cannot link " + this + " object event reference. "
                                + "It asks to update its parent room " + closestRoomParent
                                + ", but it's not a MutableRoom. Check the JSON file.");
                    }
                } else {
                    parentRoomDone = true;
                }

                for (AbstractRoom room : rooms) {
                    if (!targetRoomDone) {
                        if (evt.getUpdateTargetRoomId() != null) {
                            if (evt.getUpdateTargetRoomId().equals(room.getId())) {
                                if (room instanceof MutableRoom) {
                                    evt.setUpdateTargetRoom((MutableRoom) room);
                                    targetRoomDone = true;
                                } else {
                                    throw new Error("Cannot link " + this + " object event reference. "
                                            + "It asks to update a target room " + room
                                            + ", but it's not a MutableRoom. Check the JSON file.");
                                }
                            }
                        } else {
                            targetRoomDone = true;
                        }
                    }

                    if (!teleportRoomDone) {
                        if (evt.getTeleportsPlayerToRoomId() != null) {
                            if (evt.getTeleportsPlayerToRoomId().equals(room.getId())) {
                                evt.setTeleportsPlayerToRoom(room);
                                teleportRoomDone = true;
                            }
                        } else {
                            teleportRoomDone = true;
                        }
                    }

                    if (targetRoomDone && parentRoomDone && teleportRoomDone) {
                        break;
                    }
                }

                if (!targetRoomDone) {
                    throw new Error(
                            "Couldn't process the requested \"updateTargetRoom\" event on " + this
                                    + ". Check the JSON file for correct room IDs.");
                }

                if (!teleportRoomDone) {
                    throw new Error(
                            "Couldn't process the requested \"teleportsPlayerToRoom\" event on " + this
                                    + ". Check the JSON file for correct room IDs.");
                }
            }
        }

        if (requiredWearedItemsIdToInteract != null) {
            requiredWearedItemsToInteract = new ArrayList<>();

            for (String objId : requiredWearedItemsIdToInteract) {
                for (AbstractEntity reqItem : objects.get(objId)) {
                    if (reqItem instanceof IWearable) {
                        requiredWearedItemsToInteract.add((IWearable) reqItem);
                        break;
                    }
                }
            }
        }
    }

    public void loadObjEvents() throws SQLException {
        PreparedStatement stm;

        if (closestRoomParentId != null) {
            stm = DBManager.getConnection()
                    .prepareStatement("SELECT * FROM SAVEDATA.ObjectEvent WHERE OBJID = '" + getId()
                            + "' AND ROOMID = '" + closestRoomParentId + "'");
        } else {
            stm = DBManager.getConnection()
                    .prepareStatement(
                            "SELECT * FROM SAVEDATA.ObjectEvent WHERE OBJID = '" + getId() + "'");
        }
        ResultSet rs = stm.executeQuery();

        while (rs.next()) {
            events.add(new ObjectEvent(rs));
        }

        stm.close();
    }

    private void saveEventsOnDB() throws SQLException {
        PreparedStatement stm = DBManager.getConnection()
                .prepareStatement(
                        "INSERT INTO SAVEDATA.ObjectEvent values (?, ?, ?, ?, ?, ?, ?, ?)");

        if (getEvents() != null) {
            for (ObjectEvent evt : getEvents()) {
                stm.setString(1, getId());
                stm.setString(2, closestRoomParent != null ? closestRoomParent.getId() : null);
                stm.setString(3, evt.getEventType().toString());
                stm.setString(4, evt.getText());
                stm.setBoolean(5, evt.isUpdatingParentRoom());
                stm.setString(6, evt.getUpdateTargetRoomId());
                stm.setString(7, evt.getTeleportsPlayerToRoomId());
                stm.setBoolean(8, evt.mustDestroyOnTrigger());
                stm.executeUpdate();
            }
        }
    }

    private void saveAliasesOnDB() throws SQLException {
        PreparedStatement stm = DBManager.getConnection()
                .prepareStatement("INSERT INTO SAVEDATA.Alias values (?, ?)");

        if (alias != null) {
            for (String string : alias) {
                stm.setString(1, getId());
                stm.setString(2, string);
                stm.executeUpdate();
            }
        }
    }

    private void saveRequiredWearedItemsOnDB() throws SQLException {
        PreparedStatement stm = DBManager.getConnection().prepareStatement(
                "INSERT INTO SAVEDATA.RequiredWearedItem values (?, ?, ?)");

        if (requiredWearedItemsIdToInteract != null) {
            for (String string : requiredWearedItemsIdToInteract) {
                stm.setString(1, getId());
                stm.setString(2, string);
                stm.setString(3, failedInteractionMessage);
                stm.executeUpdate();
            }
        }
    }

    protected void saveExternalsOnDB() throws SQLException {
        saveAliasesOnDB();
        saveRequiredWearedItemsOnDB();
        saveEventsOnDB();
    }

    @Override
    protected void setKnownValuesOnStatement(PreparedStatement stm) throws SQLException {
        super.setKnownValuesOnStatement(stm);
        stm.setString(4, getClosestRoomParent() != null ? getClosestRoomParent().getId() : null);
        stm.setString(5, getParent() instanceof AbstractContainer ? getParent().getId() : null);
    }

    protected void loadLocation(ResultSet resultSet, List<AbstractRoom> allRooms, Inventory inventory)
            throws SQLException {
        String roomId = resultSet.getString(DB_ROOM_ID_COLUMN);
        String containerId = resultSet.getString(DB_CONTAINER_ID_COLUMN);

        if (roomId == null && containerId != null) {
            AbstractContainer
                    .addObjectToContainerId(this, inventory.getObjects(Inventory.Mode.UNPACK_CONTAINERS), containerId);
        } else {
            for (AbstractRoom room : allRooms) {
                if (room.getId().equals(roomId)) {
                    PlayableRoom pRoom = (PlayableRoom) room;
                    closestRoomParentId = roomId;

                    if (containerId != null) {
                        AbstractContainer
                                .addObjectToContainerId(this, pRoom.getObjects(Mode.UNPACK_CONTAINERS), containerId);
                        return;
                    }

                    pRoom.getObjects().add(this);
                }
            }
        }
    }

}
