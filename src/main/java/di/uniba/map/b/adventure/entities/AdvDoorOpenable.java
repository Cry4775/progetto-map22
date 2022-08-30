package di.uniba.map.b.adventure.entities;

import java.util.Set;
import di.uniba.map.b.adventure.type.EventType;
import di.uniba.map.b.adventure.type.Room;

public class AdvDoorOpenable extends AbstractEntity implements IOpenable {

    private boolean open = false;
    private boolean locked = false;
    private int unlockedWithItemId = 0;
    private int blockedRoomId = 0;
    private Room blockedRoom;

    public AdvDoorOpenable(int id) {
        super(id);
    }

    public AdvDoorOpenable(int id, String name) {
        super(id, name);
    }

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
    public boolean open(StringBuilder outString, AbstractEntity key) {
        if (!open && !locked) {
            open = true;

            outString.append("Hai aperto: " + getName());
            outString.append(processEvent(EventType.OPEN_UNLOCKED));

            return true;
        } else if (locked) {
            if (blockedRoom != null) {
                if (key != null) {
                    if (key.getId() == unlockedWithItemId) {
                        locked = false;
                        open = true;

                        blockedRoom.setVisible(true);

                        key.setMustDestroyFromInv(true);

                        outString.append("Hai aperto: " + getName());
                        outString.append(processEvent(EventType.OPEN_UNLOCKED));

                        return true;
                    } else {
                        outString.append("Non funziona.");
                        outString.append(processEvent(EventType.OPEN_LOCKED));
                    }
                } else {
                    outString.append("È chiusa a chiave.");
                    outString.append(processEvent(EventType.OPEN_LOCKED));
                }
            }
        } else if (open) {
            outString.append("É giá aperta.");
        }
        return false;
    }

    public Room getBlockedRoom() {
        return blockedRoom;
    }

    public void setBlockedRoom(Room blockedRoom) {
        this.blockedRoom = blockedRoom;
    }
}
