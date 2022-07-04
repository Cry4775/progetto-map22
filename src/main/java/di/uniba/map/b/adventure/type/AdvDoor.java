package di.uniba.map.b.adventure.type;

import java.util.Set;

public class AdvDoor extends AdvObject {

    private boolean openable = false;
    private boolean open = false;
    private boolean locked = false;
    private int unlockedWithItemId = 0;
    private int blockedRoomId = 0;

    public AdvDoor(int id) {
        super(id);
    }

    public AdvDoor(int id, String name) {
        super(id, name);
    }

    public AdvDoor(int id, String name, String description) {
        super(id, name, description);
    }

    public AdvDoor(int id, String name, String description, Set<String> alias) {
        super(id, name, description, alias);
    }

    public boolean isOpenable() {
        return openable;
    }

    public void setOpenable(boolean openable) {
        this.openable = openable;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public int getUnlockedWithItemId() {
        return unlockedWithItemId;
    }

    public void setUnlockedWithItemId(int unlockedWithItemId) {
        this.unlockedWithItemId = unlockedWithItemId;
    }

    public int getBlockedRoomId() {
        return blockedRoomId;
    }

    public void setBlockedRoomId(int blockedRoomId) {
        this.blockedRoomId = blockedRoomId;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }
}
