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
import engine.GameManager;
import engine.GameManager.InventoryMode;
import engine.OutputManager;
import engine.database.DBManager;

public abstract class AbstractEntity extends GameComponent {

    protected static int DB_ROOM_ID_COLUMN = 4;
    protected static int DB_CONTAINER_ID_COLUMN = 5;

    private Set<String> alias = new HashSet<>();

    private boolean mustDestroyFromInv = false;

    private GameComponent parent;

    private PlayableRoom closestRoomParent;
    private String closestRoomParentId;

    private List<ObjectEvent> events = new ArrayList<>();

    private List<String> requiredWearedItemsIdToInteract = new ArrayList<>();

    private List<IWearable> requiredWearedItemsToInteract = new ArrayList<>();

    private String failedInteractionMessage;

    private boolean actionPerformed;

    public AbstractEntity(ResultSet resultSet) throws SQLException {
        super(resultSet);

        PreparedStatement stm =
                DBManager.getConnection()
                        .prepareStatement(
                                "SELECT * FROM SAVEDATA.RequiredWearedItem WHERE ID = " + getId());
        ResultSet rs = stm.executeQuery();

        while (rs.next()) {
            requiredWearedItemsIdToInteract.add(rs.getString(2));

            if (failedInteractionMessage == null) {
                failedInteractionMessage = rs.getString(3);
            }
        }

        stm.close();

        stm = DBManager.getConnection()
                .prepareStatement("SELECT * FROM SAVEDATA.Alias WHERE ID = " + getId());
        rs = stm.executeQuery();

        while (rs.next()) {
            addAlias(rs.getString(2));
        }

        stm.close();
    }

    public void setClosestRoomParentId(String closestRoomParentId) {
        this.closestRoomParentId = closestRoomParentId;
    }

    public boolean isActionPerformed() {
        return actionPerformed;
    }

    public void setActionPerformed(boolean actionPerformed) {
        this.actionPerformed = actionPerformed;
    }

    public String getFailedInteractionMessage() {
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

    public boolean isMustDestroyFromInv() {
        return mustDestroyFromInv;
    }

    public void setMustDestroyFromInv(boolean mustDestroyFromInv) {
        this.mustDestroyFromInv = mustDestroyFromInv;
    }

    public GameComponent getParent() {
        return parent;
    }

    public void setParent(GameComponent parent) {
        this.parent = parent;
    }

    public List<String> getRequiredWearedItemsIdToInteract() {
        return requiredWearedItemsIdToInteract;
    }

    public List<IWearable> getRequiredWearedItemsToInteract() {
        return requiredWearedItemsToInteract;
    }

    public void setRequiredWearedItemsToInteract(List<IWearable> requiredWearedItemsToInteract) {
        this.requiredWearedItemsToInteract = requiredWearedItemsToInteract;
    }

    public void sendLookMessage() {
        OutputManager.append(getDescription());

        if (OutputManager.isOutputEmpty()) {
            OutputManager.append("Nulla di particolare.");
        } else {
            triggerEvent(EventType.LOOK_AT);
        }
    }

    public void triggerEvent(EventType type) {
        ObjectEvent evt = ObjectEvent.getEvent(events, type);

        if (evt != null)
            evt.trigger(this);
    }

    public void processReferences(Multimap<String, AbstractEntity> objects,
            List<AbstractRoom> rooms) {
        processRoomParent(rooms);
        processEventReferences(objects, rooms);
    }

    public void processRoomParent(List<AbstractRoom> rooms) {
        for (AbstractRoom room : rooms) {
            if (room instanceof PlayableRoom) {
                PlayableRoom pRoom = (PlayableRoom) room;

                if (pRoom.getObjects(Mode.INCLUDE_EVERYTHING).contains(this)) {
                    if (parent == null)
                        parent = pRoom;

                    for (AbstractRoom _room : AbstractRoom.getAllRooms(pRoom)) {
                        if (_room instanceof PlayableRoom) {
                            PlayableRoom _pRoom = (PlayableRoom) _room;

                            if (_pRoom.getObjects(Mode.UNPACK_CONTAINERS).contains(this)) {
                                closestRoomParent = _pRoom;
                            }
                        }
                    }
                }
            }
        }
    }

    public void processEventReferences(Multimap<String, AbstractEntity> objects,
            List<AbstractRoom> rooms) {
        if (events != null) {
            for (ObjectEvent evt : events) {
                boolean targetRoomDone = false;
                boolean parentRoomDone = false;
                boolean teleportRoomDone = false;

                if (evt.isUpdatingParentRoom()) {
                    if (parent instanceof PlayableRoom) {
                        if (parent instanceof MutableRoom) {
                            evt.setParentRoom((MutableRoom) parent);
                            parentRoomDone = true;
                        } else {
                            throw new Error("Cannot link " + getName() + " ("
                                    + getId()
                                    + ") object event reference. It asks to update its parent room, but it's not a MutableRoom. Check the JSON file.");
                        }
                    }
                }

                for (AbstractRoom room : rooms) {
                    if (!targetRoomDone) {
                        if (evt.getUpdateTargetRoomId() != null) {
                            if (evt.getUpdateTargetRoomId().equals(room.getId())) {
                                if (room instanceof MutableRoom) {
                                    evt.setUpdateTargetRoom((MutableRoom) room);
                                    targetRoomDone = true;
                                } else {
                                    throw new Error("Cannot link " + getName() + " ("
                                            + getId()
                                            + ") object event reference. It asks to update a target room, but it's not a MutableRoom. Check the JSON file.");
                                }
                            }
                        } else {
                            targetRoomDone = true;
                        }
                    }

                    if (!parentRoomDone) {
                        if (evt.isUpdatingParentRoom()) {
                            if (room instanceof MutableRoom) {
                                MutableRoom mRoom = (MutableRoom) room;
                                if (mRoom.getObjects(Mode.INCLUDE_EVERYTHING).contains(this)) {
                                    evt.setParentRoom(mRoom);
                                    parentRoomDone = true;
                                }
                            } else if (room instanceof PlayableRoom) {
                                PlayableRoom pRoom = (PlayableRoom) room;
                                if (pRoom.getObjects().contains(this)) {
                                    throw new Error("Cannot link " + getName() + " ("
                                            + getId()
                                            + ") object event reference. It asks to update its parent room, but it's not a MutableRoom. Check the JSON file.");
                                }
                            }
                        } else {
                            parentRoomDone = true;
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
                            "Couldn't find the requested \"updateTargetRoom\" event on " + getName()
                                    + " (" + getId()
                                    + "). Check the JSON file for correct room IDs.");
                }

                if (!teleportRoomDone) {
                    throw new Error(
                            "Couldn't find the requested \"teleportsPlayerToRoom\" event on "
                                    + getName()
                                    + " (" + getId()
                                    + "). Check the JSON file for correct room IDs.");
                }
            }
        }

        if (requiredWearedItemsIdToInteract != null) {
            requiredWearedItemsToInteract = new ArrayList<>();

            for (String objId : requiredWearedItemsIdToInteract) {
                for (AbstractEntity reqItem : objects.get(objId)) {
                    if (reqItem instanceof IWearable) {
                        requiredWearedItemsToInteract.add((IWearable) reqItem);
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

    public void saveEventsOnDB() throws SQLException {
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

    public void saveAliasesOnDB() throws SQLException {
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

    public void saveRequiredWearedItemsOnDB() throws SQLException {
        PreparedStatement stm =
                DBManager.getConnection().prepareStatement(
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

    public void saveExternalsOnDB() throws SQLException {
        saveAliasesOnDB();
        saveRequiredWearedItemsOnDB();
        saveEventsOnDB();
    }

    @Override
    public void setKnownValuesOnStatement(PreparedStatement stm) throws SQLException {
        super.setKnownValuesOnStatement(stm);
        stm.setString(4,
                getClosestRoomParent() != null ? getClosestRoomParent().getId() : null);
        stm.setString(5,
                getParent() instanceof AbstractContainer ? getParent().getId() : null);
    }

    public void loadLocation(ResultSet resultSet, List<AbstractRoom> allRooms) throws SQLException {
        String roomId = resultSet.getString(DB_ROOM_ID_COLUMN);
        String containerId = resultSet.getString(DB_CONTAINER_ID_COLUMN);

        if (roomId == null && containerId != null) {
            AbstractContainer.addObjectToContainerId(this,
                    GameManager.getInventory(InventoryMode.UNPACK_CONTAINERS),
                    containerId);
            return;
        } else {
            for (AbstractRoom room : allRooms) {
                if (room.getId().equals(roomId)) {
                    PlayableRoom pRoom = (PlayableRoom) room;
                    closestRoomParentId = roomId;

                    if (containerId != null) {
                        AbstractContainer.addObjectToContainerId(this,
                                pRoom.getObjects(Mode.UNPACK_CONTAINERS), containerId);
                        return;
                    }

                    pRoom.getObjects().add(this);
                }
            }
        }
    }

}
