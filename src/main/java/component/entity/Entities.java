package component.entity;

import java.util.ArrayList;
import java.util.List;
import component.entity.interfaces.IInteractable;

public final class Entities {
    private Entities() {}

    public static <T extends AbstractEntity> List<T> listCheckedEntities(Class<T> clazz,
            List<AbstractEntity> list) {
        List<T> result = new ArrayList<>();

        for (AbstractEntity obj : list) {
            if (clazz.isInstance(obj)) {
                result.add(clazz.cast(obj));
            }
        }

        return result;
    }

    public static <T extends IInteractable> List<T> listCheckedInterfaceEntities(Class<T> clazz,
            List<AbstractEntity> list) {
        List<T> result = new ArrayList<>();

        for (AbstractEntity obj : list) {
            if (clazz.isInstance(obj)) {
                result.add(clazz.cast(obj));
            }
        }

        return result;
    }
}
