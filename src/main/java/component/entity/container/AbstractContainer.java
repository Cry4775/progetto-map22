package component.entity.container;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.google.common.collect.Multimap;
import component.entity.AbstractEntity;
import component.entity.interfaces.IFillable;
import component.entity.interfaces.IFluid;
import component.entity.interfaces.IPickupable;
import component.entity.interfaces.IWearable;
import component.event.EventType;
import component.room.AbstractRoom;
import component.room.PlayableRoom;
import engine.Inventory;
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

    public boolean insert(AbstractEntity obj, Inventory inventory) {
        if (!canInteract())
            return false;

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
                    return true;
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
                        return false;
                    }
                }

                obj.setClosestRoomParent(getClosestRoomParent());
                obj.setParent(this);
                inventory.removeObject(obj);
                ((IPickupable) obj).setPickedUp(false);

                this.add(obj);

                GUIManager.appendOutput("Hai lasciato: " + obj.getName());
                triggerEvent((EventType.INSERT));
                return true;
            } else {
                GUIManager.appendOutput("Non puoi lasciare oggetti qui.");
            }
        }

        return false;
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

                setClosestRoomParentId(roomId);

                pRoom.getObjects().add(this);
            }
        }

        return null;
    }

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

    public static void addObjectToContainerId(AbstractEntity object,
            List<AbstractEntity> list, String id) {
        for (AbstractEntity obj : list) {
            if (obj instanceof AbstractContainer) {
                if (obj.getId().equals(id)) {
                    AbstractContainer container = (AbstractContainer) obj;

                    container.add(object);
                    return;
                }
            }
        }
    }
}
