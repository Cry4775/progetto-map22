package di.uniba.map.b.adventure.entities;

import java.util.Set;

public class AdvMagicWall extends AbstractEntity {

    private boolean locked = true;
    private int unlockedByWearingItemId = 0;
    private int blockedRoomId = 0;
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

}
