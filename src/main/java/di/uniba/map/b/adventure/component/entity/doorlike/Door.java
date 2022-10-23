package di.uniba.map.b.adventure.component.entity.doorlike;

import java.util.List;
import java.util.Set;
import com.google.common.collect.Multimap;
import di.uniba.map.b.adventure.component.entity.AbstractEntity;
import di.uniba.map.b.adventure.component.entity.interfaces.IOpenable;
import di.uniba.map.b.adventure.component.event.EventType;
import di.uniba.map.b.adventure.component.room.AbstractRoom;
import di.uniba.map.b.adventure.sound.SoundManager;
import di.uniba.map.b.adventure.sound.SoundManager.Mode;

public class Door extends AbstractEntity implements IOpenable {

    private boolean open = false;
    private boolean locked = false;
    private AbstractEntity unlockedWithItem;
    private Integer unlockedWithItemId;
    private int blockedRoomId = 0;
    private AbstractRoom blockedRoom;

    public Door(int id, String name, String description) {
        super(id, name, description);
    }

    public Door(int id, String name, String description, Set<String> alias) {
        super(id, name, description, alias);
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public AbstractEntity getUnlockedWithItem() {
        return unlockedWithItem;
    }

    public void setUnlockedWithItem(AbstractEntity unlockedWithItem) {
        this.unlockedWithItem = unlockedWithItem;
    }

    @Override
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

    @Override
    public boolean isLocked() {
        return locked;
    }

    @Override
    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    @Override
    public StringBuilder open(AbstractEntity key) {
        StringBuilder outString = new StringBuilder();

        boolean unlocked = false;

        if (locked) {
            if (blockedRoom != null) {
                if (unlockedWithItem.equals(key)) {
                    locked = false;
                    unlocked = true;
                    key.setMustDestroyFromInv(true);
                } else {
                    outString.append(key == null ? "É chiusa a chiave." : "Non funziona.");
                    outString.append(processEvent(EventType.OPEN_LOCKED));

                    return outString;
                }
            }
        }

        if (!open) {
            open = true;
            SoundManager.playWav(unlocked ? SoundManager.DOOR_UNLOCK_OPEN_SOUND_PATH
                    : SoundManager.DOOR_OPEN_SOUND_PATH, Mode.SOUND);
            outString.append("Hai aperto: " + getName());
            outString.append(processEvent(EventType.OPEN_UNLOCKED));

            setActionPerformed(true);
        } else {
            outString.append("É giá aperta.");
        }

        return outString;
    }

    public AbstractRoom getBlockedRoom() {
        return blockedRoom;
    }

    public void setBlockedRoom(AbstractRoom blockedRoom) {
        this.blockedRoom = blockedRoom;
    }

    @Override
    public void processReferences(Multimap<Integer, AbstractEntity> objects,
            List<AbstractRoom> rooms) {
        if (blockedRoomId != 0) {
            rooms.stream()
                    .filter(room -> blockedRoomId == room.getId())
                    .forEach(room -> blockedRoom = room);
        }

        if (unlockedWithItemId != null) {
            if (!objects.containsKey(unlockedWithItemId)) {
                throw new RuntimeException(
                        "Couldn't find the requested \"unlockedWithItem\" ID on " + getName()
                                + " (" + getId()
                                + "). Check the JSON file for correct object IDs.");
            }

            for (AbstractEntity reqItem : objects.get(unlockedWithItemId)) {
                unlockedWithItem = reqItem;
            }
        }

        processRoomParent(rooms);
        processEventReferences(objects, rooms);
    }
}
