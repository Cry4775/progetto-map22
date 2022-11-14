package engine;

import java.util.ArrayList;
import java.util.List;
import component.entity.AbstractEntity;
import component.entity.container.AbstractContainer;

public class Inventory {
    private List<AbstractEntity> objects = new ArrayList<>();

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

    public void addObject(AbstractEntity obj) {
        objects.add(obj);
    }

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
}
