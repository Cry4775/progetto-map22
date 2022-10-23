package di.uniba.map.b.adventure.component.entity.doorlike;

import java.util.List;
import java.util.Set;
import com.google.common.collect.Multimap;
import di.uniba.map.b.adventure.component.entity.AbstractEntity;
import di.uniba.map.b.adventure.component.entity.interfaces.IWearable;
import di.uniba.map.b.adventure.component.room.AbstractRoom;
import di.uniba.map.b.adventure.component.room.PlayableRoom;
import di.uniba.map.b.adventure.engine.command.CommandType;

public class InvisibleWall extends AbstractEntity {

    private boolean locked = true;

    private int blockedRoomId = 0;

    private boolean northBlocked = false;
    private boolean southBlocked = false;
    private boolean eastBlocked = false;
    private boolean westBlocked = false;
    private boolean northEastBlocked = false;
    private boolean northWestBlocked = false;
    private boolean southEastBlocked = false;
    private boolean southWestBlocked = false;
    private boolean upBlocked = false;
    private boolean downBlocked = false;

    private String trespassingWhenLockedText; // TODO é possibile rimuoverlo

    public InvisibleWall(int id, String name, String description) {
        super(id, name, description);
    }

    public InvisibleWall(int id, String name, String description, Set<String> alias) {
        super(id, name, description, alias);
    }

    @Override
    public void processReferences(Multimap<Integer, AbstractEntity> objects,
            List<AbstractRoom> rooms) {
        if (getRequiredWearedItemsIdToInteract() != null) {
            for (Integer reqId : getRequiredWearedItemsIdToInteract()) {
                if (!objects.containsKey(reqId)) {
                    throw new RuntimeException(
                            "Couldn't find the requested \"requiredWearedItemsIdToInteract\" ID "
                                    + "(" + reqId + ") on " + getName()
                                    + " (" + getId()
                                    + "). Check the JSON file for correct object IDs.");
                }

                for (AbstractEntity obj : objects.get(reqId)) {
                    if (obj instanceof IWearable) {
                        getRequiredWearedItemsToInteract().add((IWearable) obj);
                    }
                }
            }
        }

        processRoomParent(rooms);
        processEventReferences(objects, rooms);
    }

    public void processRequirements(List<AbstractEntity> inventory) {
        if (locked) {
            if (getRequiredWearedItemsToInteract() != null) {
                for (IWearable wearable : getRequiredWearedItemsToInteract()) {
                    if (!wearable.isWorn()) {
                        return;
                    }
                }
                locked = false;
            }
        } else {
            if (getRequiredWearedItemsToInteract() != null) {
                for (IWearable wearable : getRequiredWearedItemsToInteract()) {
                    if (!wearable.isWorn()) {
                        locked = true;
                        return;
                    }
                }
            }
        }
    }

    public boolean isBlocking(CommandType direction) {
        PlayableRoom parentRoom = (PlayableRoom) getParent();
        AbstractRoom nextRoom = parentRoom.getRoomAt(direction);

        if (blockedRoomId != 0) {
            if (nextRoom != null && nextRoom.getId() == blockedRoomId) {
                return true;
            }
        } else {
            // If the wall is blocking a "fake" room we use those booleans

            switch (direction) {
                case NORTH:
                    return northBlocked;
                case NORTH_EAST:
                    return northEastBlocked;
                case NORTH_WEST:
                    return northWestBlocked;
                case SOUTH:
                    return southBlocked;
                case SOUTH_EAST:
                    return southEastBlocked;
                case SOUTH_WEST:
                    return southWestBlocked;
                case EAST:
                    return eastBlocked;
                case WEST:
                    return westBlocked;
                case UP:
                    return upBlocked;
                case DOWN:
                    return downBlocked;
                default:
                    return false;
            }
        }

        return false;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public int getBlockedRoomId() {
        return blockedRoomId;
    }

    public void setBlockedRoomId(int blockedRoomId) {
        this.blockedRoomId = blockedRoomId;
    }

    public String getTrespassingWhenLockedText() {
        return trespassingWhenLockedText;
    }

    public void setTrespassingWhenLockedText(String trespassingWhenLockedText) {
        this.trespassingWhenLockedText = trespassingWhenLockedText;
    }

    public boolean isNorthBlocked() {
        return northBlocked;
    }

    public void setNorthBlocked(boolean northBlocked) {
        this.northBlocked = northBlocked;
    }

    public boolean isSouthBlocked() {
        return southBlocked;
    }

    public void setSouthBlocked(boolean southBlocked) {
        this.southBlocked = southBlocked;
    }

    public boolean isEastBlocked() {
        return eastBlocked;
    }

    public void setEastBlocked(boolean eastBlocked) {
        this.eastBlocked = eastBlocked;
    }

    public boolean isWestBlocked() {
        return westBlocked;
    }

    public void setWestBlocked(boolean westBlocked) {
        this.westBlocked = westBlocked;
    }

    public boolean isNorthEastBlocked() {
        return northEastBlocked;
    }

    public void setNorthEastBlocked(boolean northEastBlocked) {
        this.northEastBlocked = northEastBlocked;
    }

    public boolean isNorthWestBlocked() {
        return northWestBlocked;
    }

    public void setNorthWestBlocked(boolean northWestBlocked) {
        this.northWestBlocked = northWestBlocked;
    }

    public boolean isSouthEastBlocked() {
        return southEastBlocked;
    }

    public void setSouthEastBlocked(boolean southEastBlocked) {
        this.southEastBlocked = southEastBlocked;
    }

    public boolean isSouthWestBlocked() {
        return southWestBlocked;
    }

    public void setSouthWestBlocked(boolean southWestBlocked) {
        this.southWestBlocked = southWestBlocked;
    }

    public boolean isUpBlocked() {
        return upBlocked;
    }

    public void setUpBlocked(boolean upBlocked) {
        this.upBlocked = upBlocked;
    }

    public boolean isDownBlocked() {
        return downBlocked;
    }

    public void setDownBlocked(boolean downBlocked) {
        this.downBlocked = downBlocked;
    }

}
