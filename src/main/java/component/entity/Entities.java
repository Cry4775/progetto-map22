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

    /**
     * Checks for requested class objects in the given list and returns
     * them casted in a list as a result.
     * 
     * @param <T> the class you want to check for.
     * @param clazz the class object you want to check for.
     * @param list the general list to check from.
     * @return a list of the requested class objects retrieved from the source list.
     */
    public static <T extends AbstractEntity> List<T> listCheckedEntities(Class<T> clazz,
            List<AbstractEntity> list) {
        return Utils.listCheckedObjects(clazz, list);
    }

    /**
     * Checks for requested interface objects in the given list and returns
     * them casted in a list as a result.
     * 
     * @param <T> the class you want to check for.
     * @param clazz the class object you want to check for.
     * @param list the general list to check from.
     * @return a list of the requested class objects retrieved from the source list.
     */
    public static <T extends IInteractable> List<T> listCheckedInterfaceEntities(Class<T> clazz,
            List<AbstractEntity> list) {
        return Utils.listCheckedObjects(clazz, list);
    }

    /**
     * @param rooms
     * @return a multimap (the same key can contain more values) of all the room possible objects
     *         (including inner mutables and containers).
     */
    public static Multimap<String, AbstractEntity> mapRoomsObjects(List<AbstractRoom> rooms) {
        Multimap<String, AbstractEntity> result = ArrayListMultimap.create();

        for (PlayableRoom room : Rooms.listCheckedRooms(PlayableRoom.class, rooms)) {
            result.putAll(room.getObjectsAsMap(PlayableRoom.Mode.INCLUDE_EVERYTHING));
        }

        return result;
    }
}
