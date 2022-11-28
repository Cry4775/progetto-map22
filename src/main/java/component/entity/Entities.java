package component.entity;

import java.util.List;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import component.entity.interfaces.IInteractable;
import component.room.AbstractRoom;
import component.room.PlayableRoom;
import component.room.Rooms;
import utility.Utils;

public final class Entities {
    private Entities() {}

    public static <T extends AbstractEntity> List<T> listCheckedEntities(Class<T> clazz,
            List<AbstractEntity> list) {
        return Utils.listCheckedObjects(clazz, list);
    }

    public static <T extends IInteractable> List<T> listCheckedInterfaceEntities(Class<T> clazz,
            List<AbstractEntity> list) {
        return Utils.listCheckedObjects(clazz, list);
    }

    public static Multimap<String, AbstractEntity> mapRoomsObjects(List<AbstractRoom> rooms) {
        Multimap<String, AbstractEntity> result = ArrayListMultimap.create();

        for (PlayableRoom room : Rooms.listCheckedRooms(PlayableRoom.class, rooms)) {
            result.putAll(room.getObjectsAsMap(PlayableRoom.Mode.INCLUDE_EVERYTHING));
        }

        return result;
    }
}
