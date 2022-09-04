package di.uniba.map.b.adventure.entities;

import java.util.List;
import java.util.Set;
import di.uniba.map.b.adventure.type.CommandType;
import di.uniba.map.b.adventure.type.Room;

public class AdvMagicWall extends AbstractEntity {

    private boolean locked = true;
    private int unlockedByWearingItemId = 0; // TODO reference?
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

    private String trespassingWhenLockedText;

    public AdvMagicWall(int id, String name, String description, Set<String> alias) {
        super(id, name, description, alias);
    }

    public AdvMagicWall(int id) {
        super(id);
    }

    public AdvMagicWall(int id, String name) {
        super(id, name);
    }

    public AdvMagicWall(int id, String name, String description) {
        super(id, name, description);
    }

    public void processRequirements(List<AbstractEntity> inventory) {
        if (locked && unlockedByWearingItemId != 0) {
            for (AbstractEntity obj : inventory) {
                if (obj.getId() == unlockedByWearingItemId) {
                    if (obj instanceof IWearable) {
                        IWearable wearable = (IWearable) obj;
                        if (wearable.isWorn()) {
                            locked = false;
                            break;
                        }
                    }
                }
            }
        }
    }

    public boolean isDirectionBlocked(CommandType direction, Room nextRoom) {
        if (locked) {
            if (nextRoom != null) {
                if (blockedRoomId == nextRoom.getId()) {
                    return true;
                }
            }

            switch (direction) {
                case NORTH:
                    return northBlocked;
                case SOUTH:
                    return southBlocked;
                case EAST:
                    return eastBlocked;
                case WEST:
                    return westBlocked;
                case NORTH_EAST:
                    return northEastBlocked;
                case NORTH_WEST:
                    return northWestBlocked;
                case SOUTH_EAST:
                    return southEastBlocked;
                case SOUTH_WEST:
                    return southWestBlocked;
                case UP:
                    return upBlocked;
                case DOWN:
                    return downBlocked;
                default:
                    return true;
            }
        } else {
            return false;
        }
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public int getUnlockedByWearingItemId() {
        return unlockedByWearingItemId;
    }

    public void setUnlockedByWearingItemId(int unlockedByWearingItemId) {
        this.unlockedByWearingItemId = unlockedByWearingItemId;
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

    @Override
    public void processReferences(List<AbstractEntity> objects, List<Room> rooms) {
        processEventReferences(objects, rooms);
    }

}
