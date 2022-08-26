/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package di.uniba.map.b.adventure.type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author pierpaolo
 */
public abstract class AdvObject {

    private final int id;

    private String name;

    private String description;

    private Set<String> alias;

    private boolean mustDestroyFromInv = false;

    private boolean pickupable = false;

    private boolean pickupableWithFillable = false;

    private boolean picked = false; // TODO forse c'é un modo migliore

    private AdvObject parent;

    private final List<ObjEvent> events = new ArrayList<>();

    public AdvObject(int id) {
        this.id = id;
    }

    public AdvObject(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public AdvObject(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public AdvObject(int id, String name, String description, Set<String> alias) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.alias = alias;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<String> getAlias() {
        return alias;
    }

    public void setAlias(Set<String> alias) {
        this.alias = alias;
    }

    public void setAlias(String[] alias) {
        this.alias = new HashSet<>(Arrays.asList(alias));
    }

    public int getId() {
        return this.id;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AdvObject other = (AdvObject) obj;
        if (id != other.id)
            return false;
        return true;
    }

    public List<ObjEvent> getEvents() {
        return events;
    }

    public ObjEvent getEvent(EventType type) {
        if (getEvents() != null) {
            for (ObjEvent evt : getEvents()) {
                if (evt.getEventType() == type) {
                    if (!evt.isTriggered()) {
                        return evt;
                    }
                }
            }
        }
        return null;
    }

    public boolean isMustDestroyFromInv() {
        return mustDestroyFromInv;
    }

    public void setMustDestroyFromInv(boolean mustDestroyFromInv) {
        this.mustDestroyFromInv = mustDestroyFromInv;
    }

    public AdvObject getParent() {
        return parent;
    }

    public void setParent(AdvObject parent) {
        this.parent = parent;
    }

    public boolean isPickupable() {
        return pickupable;
    }

    public void setPickupable(boolean pickupable) {
        this.pickupable = pickupable;
    }

    public boolean isPickupableWithFillable() {
        return pickupableWithFillable;
    }

    public void setPickupableWithFillable(boolean pickupableWithFillable) {
        this.pickupableWithFillable = pickupableWithFillable;
    }

    public boolean isPicked() {
        return picked;
    }

    public void setPicked(boolean picked) {
        this.picked = picked;
    }

    public boolean pickup(StringBuilder outString, List<AdvObject> inventory,
            List<AdvObject> roomObjects) {
        if (pickupableWithFillable) {
            boolean canProceed = false;
            for (AdvObject invObject : inventory) {
                if (invObject instanceof IFillable) {
                    IFillable invFillable = (IFillable) invObject;

                    canProceed = invFillable.fill(this);

                    if (canProceed) {
                        break;
                    }
                }
            }
            if (!canProceed) {
                outString.append("Non puoi prenderlo senza lo strumento adatto.");
                return false;
            }
        } else if (parent != null && parent instanceof AdvItemFillable) {
            outString.append("Non puoi.");
            return false;
        } else {
            picked = true;
            inventory.add(this);

            // Check if it's an obj inside something and remove it from its list
            if (parent != null) {
                AbstractContainer parentContainer = (AbstractContainer) parent;
                parentContainer.getList().remove(this);
                parent = null;
            } else {
                roomObjects.remove(this);
            }

            outString.append("Hai raccolto: " + name);
            // outString.append(handleObjEvent(item.getEvent(EventType.PICK_UP)));
        }
        return true;
    }
}
