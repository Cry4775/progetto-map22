package component.entity.container;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.google.common.collect.Multimap;
import component.entity.AbstractEntity;
import component.entity.Entities;
import component.entity.interfaces.IFillable;
import component.entity.interfaces.IFluid;
import component.entity.interfaces.IPickupable;
import component.entity.interfaces.IWearable;
import component.event.EventType;
import component.room.AbstractRoom;
import component.room.PlayableRoom;
import engine.Inventory;
import engine.MoveInformations.ActionState;
import gui.GUIManager;
import utility.Triple;

public abstract class AbstractContainer extends AbstractEntity {

    protected boolean contentRevealed = false;
    private List<AbstractEntity> list = new ArrayList<>();
    private boolean forFluids;

    public AbstractContainer(ResultSet resultSet) throws SQLException {
        super(resultSet);
    }

    public void setForFluids(boolean forFluids) {
        this.forFluids = forFluids;
    }

    public boolean isForFluids() {
        return forFluids;
    }

    public List<AbstractEntity> getList() {
        return list;
    }

    public void add(AbstractEntity o) {
        list.add(o);
    }

    public void removeObject(AbstractEntity o) {
        list.remove(o);
        o.setParent(null);
        o.setClosestRoomParent(null);
    }

    public boolean isContentRevealed() {
        return contentRevealed;
    }

    @Override
    public void lookAt() {
        super.lookAt();

        GUIManager.appendOutput(getContentString());
    }

    /**
     * @return a string representing the content of this container.
     */
    public StringBuilder getContentString() {
        if (!contentRevealed) {
            contentRevealed = true;
        }

        if (getList() != null && !getList().isEmpty()) {
            StringBuilder outString = new StringBuilder("\nCi trovi:");

            for (AbstractEntity advObject : getList()) {
                outString.append(" " + advObject.getName() + ",");
            }

            outString.deleteCharAt(outString.length() - 1);

            return outString;
        }
        return new StringBuilder();
    }

    /**
     * Executes the "Insert" command.
     * 
     * @param obj the object to insert.
     * @param inventory the inventory reference.
     * @return the action state.
     */
    public ActionState insert(AbstractEntity obj, Inventory inventory) {
        if (!canInteract())
            return ActionState.NO_MOVE;

        if (obj instanceof IFluid) {
            if (forFluids) {
                IFluid fluid = (IFluid) obj;

                if (obj.getParent() instanceof IFillable) {
                    IFillable fluidContainer = (IFillable) obj.getParent();

                    fluidContainer.setFilled(false);
                    obj.setClosestRoomParent(getClosestRoomParent());
                    obj.setParent(this);
                    fluid.setPickedUp(false);
                    this.add(obj);

                    GUIManager.appendOutput("Hai versato: " + obj.getName());
                    triggerEvent(EventType.INSERT);
                    return ActionState.NORMAL_ACTION;
                }
            } else {
                GUIManager.appendOutput("Non puoi versare liquidi qui.");
            }
        } else {
            if (!forFluids) {
                if (obj.getParent() instanceof AbstractContainer) {
                    AbstractContainer container = (AbstractContainer) obj.getParent();

                    container.removeObject(obj);
                }

                if (obj instanceof IWearable) {
                    IWearable wearable = (IWearable) obj;

                    if (wearable.isWorn()) {
                        GUIManager.appendOutput("Devi prima toglierlo di dosso.");
                        return ActionState.NO_MOVE;
                    }
                }

                obj.setClosestRoomParent(getClosestRoomParent());
                obj.setParent(this);
                inventory.removeObject(obj);
                ((IPickupable) obj).setPickedUp(false);

                this.add(obj);

                GUIManager.appendOutput("Hai lasciato: " + obj.getName());
                triggerEvent(EventType.INSERT);
                return ActionState.NORMAL_ACTION;
            } else {
                GUIManager.appendOutput("Non puoi lasciare oggetti qui.");
            }
        }

        return ActionState.NO_MOVE;
    }

    @Override
    public void processReferences(Multimap<String, AbstractEntity> objects,
            List<AbstractRoom> rooms) {
        super.processReferences(objects, rooms);

        if (list != null) {
            for (AbstractEntity item : list) {
                item.setParent(this);
            }
        } else {
            list = new ArrayList<>();
        }
    }

    /**
     * Loads this object's room location from DB.
     * 
     * @param resultSet the query result set of this class objects.
     * @param allRooms all the possible rooms list.
     * @throws SQLException
     */
    public Triple<AbstractEntity, String, String> loadRoomLocation(ResultSet resultSet,
            List<AbstractRoom> allRooms) throws SQLException {
        String roomId = resultSet.getString(DB_ROOM_ID_COLUMN);
        String containerId = resultSet.getString(DB_CONTAINER_ID_COLUMN);

        if (containerId != null) {
            return new Triple<AbstractEntity, String, String>(this, roomId, containerId);
        }

        for (AbstractRoom room : allRooms) {
            if (roomId.equals(room.getId())) {
                PlayableRoom pRoom = (PlayableRoom) room;

                setClosestRoomParent(pRoom);

                pRoom.getObjects().add(this);
            }
        }

        return null;
    }

    /**
     * @param obj the object you want to check.
     * @return a list of objects inside the requested object.
     */
    public static List<AbstractEntity> getAllObjectsInside(AbstractEntity obj) {
        List<AbstractEntity> result = new ArrayList<>();

        if (obj instanceof AbstractContainer) {
            AbstractContainer container = (AbstractContainer) obj;

            if (container.getList() != null) {
                for (AbstractEntity insideObj : container.getList()) {
                    result.addAll(getAllObjectsInside(insideObj));
                    result.add(insideObj);
                }
            }
        }

        return result;
    }

    /**
     * Adds the object to the requested container ID.
     * 
     * @param object the object to add.
     * @param list the list of objects where the container is located.
     * @param id the container ID you wish to add the object to.
     */
    public static void addObjectToContainerId(AbstractEntity object, List<AbstractEntity> list, String id) {
        for (AbstractContainer container : Entities.listCheckedEntities(AbstractContainer.class, list)) {
            if (container.getId().equals(id)) {
                container.add(object);
                return;
            }
        }
    }
}
