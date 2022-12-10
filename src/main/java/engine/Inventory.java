package engine;

import java.util.ArrayList;
import java.util.List;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import component.entity.AbstractEntity;
import component.entity.container.AbstractContainer;

public class Inventory {
    private List<AbstractEntity> objects = new ArrayList<>();

    /**
     * Getter modes:
     * <ul>
     * <li><b>NORMAL</b>: gets the normal list of objects.</li>
     * <li><b>UNPACK_CONTAINERS</b>: gets the list of objects including eventual
     * objects in all the containers.</li>
     * </ul>
     */
    public enum Mode {
        NORMAL,
        UNPACK_CONTAINERS
    }

    public boolean isEmpty() {
        return objects.isEmpty();
    }

    public List<AbstractEntity> getObjects() {
        return objects;
    }

    /**
     * @param mode the desired mode.
     * @return the list of objects in the desired state.
     */
    public List<AbstractEntity> getObjects(Mode mode) {
        switch (mode) {
            case NORMAL:
                return objects;
            case UNPACK_CONTAINERS:
                List<AbstractEntity> result = new ArrayList<>();

                for (AbstractEntity obj : objects) {
                    result.add(obj);
                    result.addAll(AbstractContainer.getAllObjectsInside(obj));
                }

                return result;
            default:
                return objects;
        }
    }

    /**
     * Adds the desired object to inventory.
     * 
     * @param obj the object to add.
     */
    public void addObject(AbstractEntity obj) {
        objects.add(obj);
    }

    /**
     * Removes the desired object from inventory,
     * also checks inside containers if necessary.
     * 
     * @param obj the object to remove.
     */
    public void removeObject(AbstractEntity obj) {
        if (objects.contains(obj)) {
            objects.remove(obj);
        } else {
            if (obj.getParent() instanceof AbstractContainer) {
                AbstractContainer container = (AbstractContainer) obj.getParent();
                container.removeObject(obj);
            } else {
                throw new Error("Couldn't destroy item " + obj.getName() + " (" + obj.getId() + ")");
            }
        }
    }

    /**
     * Performs a check on the inventory,
     * searching for items that must be destroyed (removed) after use.
     */
    public void checkForDestroyableItems() {
        for (AbstractEntity obj : getObjects(Mode.UNPACK_CONTAINERS)) {
            if (obj.mustDestroyFromInventory()) {
                removeObject(obj);
            }
        }
    }

    /**
     * @return a multimap (the same key can contain more values) of all the objects
     *         contained in the inventory (including the objects in containers).
     */
    public Multimap<String, AbstractEntity> mapAllObjects() {
        Multimap<String, AbstractEntity> result = ArrayListMultimap.create();

        for (AbstractEntity obj : getObjects(Inventory.Mode.UNPACK_CONTAINERS)) {
            result.put(obj.getId(), obj);
        }

        return result;
    }
}
