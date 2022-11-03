package component.entity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.google.common.collect.Multimap;
import component.GameComponent;
import component.entity.container.AbstractContainer;
import component.entity.interfaces.IFillable;
import component.entity.interfaces.IOpenable;
import component.entity.interfaces.IWearable;
import component.event.EventType;
import component.event.ObjectEvent;
import component.room.AbstractRoom;
import component.room.MutableRoom;
import component.room.PlayableRoom;
import engine.GameManager;
import engine.database.DBManager;

public abstract class AbstractEntity extends GameComponent {

    protected static int DB_ROOM_ID_COLUMN = 4;
    protected static int DB_CONTAINER_ID_COLUMN = 5;

    private Set<String> alias = new HashSet<>();

    private boolean mustDestroyFromInv = false;

    private GameComponent parent;

    private PlayableRoom closestRoomParent;

    private List<ObjectEvent> events = new ArrayList<>();

    private List<String> requiredWearedItemsIdToInteract = new ArrayList<>();

    private List<IWearable> requiredWearedItemsToInteract = new ArrayList<>();

    private String failedInteractionMessage;

    private boolean actionPerformed;

    public AbstractEntity(ResultSet resultSet) throws SQLException {
        super(resultSet);

        PreparedStatement stm =
                DBManager.getConnection()
                        .prepareStatement("SELECT * FROM SAVEDATA.RequiredWearedItem");
        ResultSet rs = stm.executeQuery();

        while (rs.next()) {
            if (rs.getString(1).equals(getId())) {
                requiredWearedItemsIdToInteract.add(rs.getString(2));

                if (failedInteractionMessage == null) {
                    failedInteractionMessage = rs.getString(3);
                }
            }
        }

        stm.close();

        stm = DBManager.getConnection().prepareStatement("SELECT * FROM SAVEDATA.Alias");
        rs = stm.executeQuery();

        while (rs.next()) {
            if (rs.getString(1).equals(getId())) {
                addAlias(rs.getString(2));
            }
        }

        stm.close();

        stm = DBManager.getConnection().prepareStatement("SELECT * FROM SAVEDATA.ObjectEvent");
        rs = stm.executeQuery();

        while (rs.next()) {
            if (rs.getString(1).equals(getId())) {
                events.add(new ObjectEvent(rs));
            }
        }

        stm.close();
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

    public void setFailedInteractionMessage(String failedInteractionMessage) {
        this.failedInteractionMessage = failedInteractionMessage;
    }

    public Set<String> getAlias() {
        return alias;
    }

    public void setAlias(Set<String> alias) {
        this.alias = alias;
    }

    public void setAlias(String[] alias) {
        this.alias = new HashSet<>(Arrays.asList(alias));
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

    public abstract void processReferences(Multimap<String, AbstractEntity> objects,
            List<AbstractRoom> rooms);

    public void processRoomParent(List<AbstractRoom> rooms) {
        for (AbstractRoom room : rooms) {
            if (room instanceof MutableRoom) {
                MutableRoom mRoom = (MutableRoom) room;

                if (mRoom.getAllPossibleObjects().contains(this)) {
                    if (parent == null)
                        parent = room;

                    if (mRoom.getObjects() != null) {
                        if (mRoom.getAllObjects().contains(this)) {
                            closestRoomParent = mRoom;
                        } else {
                            for (AbstractRoom _room : mRoom.getAllRooms()) {
                                if (_room instanceof PlayableRoom) {
                                    PlayableRoom pRoom = (PlayableRoom) _room;

                                    if (pRoom.getObjects() != null) {
                                        if (pRoom.getAllObjects().contains(this)) {
                                            closestRoomParent = pRoom;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else if (room instanceof PlayableRoom) {
                PlayableRoom playableRoom = (PlayableRoom) room;

                if (playableRoom.getObjects() != null) {
                    if (playableRoom.getAllObjects().contains(this)) {
                        if (parent == null)
                            parent = room;
                        closestRoomParent = playableRoom;
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
                                if (mRoom.getAllPossibleObjects().contains(this)) {
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

    public List<ObjectEvent> getEvents() {
        return events;
    }

    public ObjectEvent getEvent(EventType type) {
        if (getEvents() != null) {
            for (ObjectEvent evt : getEvents()) {
                if (evt.getEventType() == type) {
                    if (!evt.isTriggered()) {
                        return evt;
                    }
                }
            }
        }
        return null;
    }

    public StringBuilder processEvent(EventType eventType) {
        StringBuilder outString = new StringBuilder();

        ObjectEvent evt = getEvent(eventType);

        if (evt != null) {
            if (evt.isUpdatingParentRoom()) {
                evt.getParentRoom().updateToNewRoom();
            }

            if (evt.getUpdateTargetRoom() != null) {
                evt.getUpdateTargetRoom().updateToNewRoom();
            }

            if (evt.mustDestroyOnTrigger()) {
                if (parent instanceof AbstractContainer) {
                    AbstractContainer container = (AbstractContainer) parent;

                    container.remove(this);
                } else if (parent instanceof PlayableRoom) {
                    PlayableRoom pRoom = (PlayableRoom) parent;

                    pRoom.removeObject(this);
                } else {
                    throw new Error(
                            "Couldn't find the parent room of " + getName()
                                    + " (" + getId() + ").");
                }
            }

            if (evt.getText() != null && !evt.getText().isEmpty()) {
                outString.append("\n\n" + evt.getText());
            }

            evt.setTriggered(true);
        }
        return outString;
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

    public StringBuilder getLookMessage() {
        StringBuilder outString = new StringBuilder();

        if (getDescription() != null) {
            outString.append(getDescription());
        }

        if (this instanceof IOpenable) {
            IOpenable openableObj = (IOpenable) this;
            if (openableObj.isLocked()) {
                outString.append("È chiuso a chiave.");
            } else if (!openableObj.isOpen()) {
                outString.append("È chiuso.");
            } else if (openableObj.isOpen()) {
                outString.append("È aperto.");
                if (openableObj instanceof AbstractContainer) {
                    AbstractContainer container = (AbstractContainer) openableObj;
                    outString.append(container.getContentString());
                }
            }
        } else if (this instanceof AbstractContainer) {
            AbstractContainer container = (AbstractContainer) this;
            outString.append(container.getContentString());
        } else if (this instanceof IFillable) {
            IFillable fillable = (IFillable) this;
            if (fillable.isFilled()) {
                outString.append("\nÉ pieno di: " + fillable.getEligibleItem().getName());
            } else {
                outString.append("\nÈ vuoto.");
            }
        }

        if (outString.toString().isEmpty()) {
            outString.append("Nulla di particolare.");
        } else {
            outString.append(processEvent(EventType.LOOK_AT));
        }

        return outString;
    }

    public void setEvents(List<ObjectEvent> events) {
        this.events = events;
    }

    public void setRequiredWearedItemsIdToInteract(List<String> requiredWearedItemsIdToInteract) {
        this.requiredWearedItemsIdToInteract = requiredWearedItemsIdToInteract;
    }

    public void setRequiredWearedItemsToInteract(List<IWearable> requiredWearedItemsToInteract) {
        this.requiredWearedItemsToInteract = requiredWearedItemsToInteract;
    }

    public void saveEventsOnDB(Connection connection) throws SQLException {
        PreparedStatement stm = connection.prepareStatement(
                "INSERT INTO SAVEDATA.ObjectEvent values (?, ?, ?, ?, ?, ?, ?)");

        if (getEvents() != null) {
            for (ObjectEvent evt : getEvents()) {
                stm.setString(1, getId());
                stm.setString(2, evt.getEventType().toString());
                stm.setString(3, evt.getText());
                stm.setBoolean(4, evt.isUpdatingParentRoom());
                stm.setString(5, evt.getUpdateTargetRoomId());
                stm.setString(6, evt.getTeleportsPlayerToRoomId());
                stm.setBoolean(7, evt.mustDestroyOnTrigger());
                stm.executeUpdate();
            }
        }
    }

    public void saveAliasesOnDB(Connection connection) throws SQLException {
        PreparedStatement stm =
                connection.prepareStatement("INSERT INTO SAVEDATA.Alias values (?, ?)");

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
                stm.setString(3,
                        failedInteractionMessage == null ? "null" : failedInteractionMessage);
                stm.executeUpdate();
            }
        }
    }

    public void saveExternalsOnDB(Connection connection) throws SQLException {
        saveAliasesOnDB(connection);
        saveRequiredWearedItemsOnDB();
        saveEventsOnDB(connection);
    }

    public void setValuesOnStatement(PreparedStatement stm) throws SQLException {
        super.setValuesOnStatement(stm);
        stm.setString(4,
                getClosestRoomParent() != null ? getClosestRoomParent().getId() : "null");
        stm.setString(5,
                getParent() instanceof AbstractContainer ? getParent().getId() : "null");
    }

    public void loadLocation(ResultSet resultSet, List<AbstractRoom> allRooms,
            List<AbstractContainer> allContainers)
            throws SQLException {
        String roomId = resultSet.getString(DB_ROOM_ID_COLUMN);
        String containerId = resultSet.getString(DB_CONTAINER_ID_COLUMN);

        if (roomId.equals("null") && !containerId.equals("null")) {
            for (AbstractEntity obj : GameManager.getFullInventory()) {
                if (obj.getId().equals(containerId)) {
                    AbstractContainer container = (AbstractContainer) obj;

                    container.add(this);
                    return;
                }
            }
        } else {
            for (AbstractRoom room : allRooms) {
                if (roomId.equals(room.getId())) {
                    PlayableRoom pRoom = (PlayableRoom) room;

                    if (!containerId.equals("null")) {
                        for (AbstractEntity obj : pRoom.getAllObjects()) {
                            if (obj instanceof AbstractContainer) {
                                if (obj.getId().equals(containerId)) {
                                    AbstractContainer container = (AbstractContainer) obj;

                                    container.add(this);
                                    return;
                                }
                            }
                        }
                    }

                    pRoom.getObjects().add(this);
                }
            }
        }
    }

}
