/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package di.uniba.map.b.adventure.entities.container;

import java.util.List;
import java.util.Set;
import com.google.common.collect.Multimap;
import di.uniba.map.b.adventure.entities.AbstractEntity;
import di.uniba.map.b.adventure.entities.IOpenable;
import di.uniba.map.b.adventure.entities.pickupable.AdvItem;
import di.uniba.map.b.adventure.type.AbstractRoom;
import di.uniba.map.b.adventure.type.EventType;

public class AdvChest extends AbstractContainer implements IOpenable {
    private boolean open = false;
    private boolean locked = false;

    private AbstractEntity unlockedWithItem;
    private Integer unlockedWithItemId;

    public AdvChest(int id, String name, String description) {
        super(id, name, description);
    }

    public AdvChest(int id, String name, String description, Set<String> alias) {
        super(id, name, description, alias);
    }

    @Override
    public boolean isOpen() {
        return open;
    }

    @Override
    public void setOpen(boolean open) {
        this.open = open;
    }

    @Override
    public StringBuilder open(AbstractEntity key) {
        StringBuilder outString = new StringBuilder();

        if (locked) {
            if (unlockedWithItem.equals(key)) {
                locked = false;
                key.setMustDestroyFromInv(true);
            } else {
                outString.append(key == null ? "É chiusa a chiave." : "Non funziona.");
                outString.append(processEvent(EventType.OPEN_LOCKED));
                return outString;
            }
        }

        if (!open) {
            open = true;
            outString.append("Hai aperto: " + getName());
            outString.append(getContentString());
            outString.append(processEvent(EventType.OPEN_CONTAINER));

            setActionPerformed(true);
        } else {
            outString.append("É giá aperta. ");
            outString.append(getContentString());
        }
        return outString;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    @Override
    public int getUnlockedWithItemId() {
        return unlockedWithItemId;
    }

    public AbstractEntity getUnlockedWithItem() {
        return unlockedWithItem;
    }

    public void setUnlockedWithItem(AdvItem unlockedWithItem) {
        this.unlockedWithItem = unlockedWithItem;
    }

    @Override
    public void processReferences(Multimap<Integer, AbstractEntity> objects,
            List<AbstractRoom> rooms) {
        super.processReferences(objects, rooms);

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
    }
}
