package component.entity;

import java.util.ArrayList;
import java.util.List;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import component.entity.interfaces.IInteractable;
import component.room.AbstractRoom;
import component.room.PlayableRoom;

public final class Entities {
    private Entities() {}

    public static <T extends AbstractEntity> List<T> listCheckedEntities(Class<T> clazz,
            List<AbstractEntity> list) {
        List<T> result = new ArrayList<>();

        if (list != null) {
            for (AbstractEntity obj : list) {
                if (clazz.isInstance(obj)) {
                    result.add(clazz.cast(obj));
                }
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

    public static Multimap<String, AbstractEntity> mapRoomsObjects(List<AbstractRoom> rooms) {
        Multimap<String, AbstractEntity> result = ArrayListMultimap.create();

        for (AbstractRoom room : rooms) {
            if (room instanceof PlayableRoom) {
                PlayableRoom pRoom = (PlayableRoom) room;

                result.putAll(pRoom.getObjectsAsMap(PlayableRoom.Mode.INCLUDE_EVERYTHING));
            }
        }

        return result;
    }
}
