package di.uniba.map.b.adventure.entities;

import java.util.List;
import java.util.Set;
import com.google.common.collect.Multimap;
import di.uniba.map.b.adventure.type.EventType;
import di.uniba.map.b.adventure.type.AbstractRoom;

public class AdvDoorOpenable extends AbstractEntity implements IOpenable {

    private boolean open = false;
    private boolean locked = false;
    private AbstractEntity unlockedWithItem;
    private Integer unlockedWithItemId;
    private int blockedRoomId = 0;
    private AbstractRoom blockedRoom;

    public AdvDoorOpenable(int id, String name, String description) {
        super(id, name, description);
    }

    public AdvDoorOpenable(int id, String name, String description, Set<String> alias) {
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

        if (locked) {
            if (blockedRoom != null) {
                if (unlockedWithItem.equals(key)) {
                    locked = false;
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
