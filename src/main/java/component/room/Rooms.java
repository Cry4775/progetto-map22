package component.room;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import utility.Utils;

public class Rooms {
    private Rooms() {}

    /**
     * @param room the room to "unpack".
     * @return list of all the requested room's children, if any, and itself.
     */
    public static List<AbstractRoom> getAllRooms(AbstractRoom room) {
        List<AbstractRoom> result = new ArrayList<>();

        result.add(room);

        if (room instanceof MutableRoom) {
            MutableRoom mRoom = (MutableRoom) room;
            result.addAll(mRoom.getAllRooms());
        }

        return result;
    }

    /**
     * @param rooms the list of rooms you want to "unpack".
     * @return a list of all the rooms, including the inner mutable ones.
     */
    public static List<AbstractRoom> listAllRooms(List<AbstractRoom> rooms) {
        List<AbstractRoom> result = new ArrayList<>();

        for (AbstractRoom room : rooms) {
            result.addAll(Rooms.getAllRooms(room));
        }

        return result;
    }

    /**
     * Checks for requested class objects in the given list and lists
     * them casted in a list as result.
     * 
     * @param <T> the class you want to check for.
     * @param clazz the class object you want to check for.
     * @param list the general list to check from.
     * @return a list of the requested class objects retrieved from the source list.
     */
    public static <T extends AbstractRoom> List<T> listCheckedRooms(Class<T> clazz,
            List<AbstractRoom> list) {
        return Utils.listCheckedObjects(clazz, list);
    }

    /**
     * Checks the direction's ID and searches for the correspondant room to link them.
     * 
     * @param <T> the room class you want to check for.
     * @param rooms the rooms list.
     * @param directionIdGetter the getter for the requested direction ID.
     * @param directionSetter the setter for the correspondant direction.
     * @param clazz the room class object you want to check for.
     */
    public static <T extends AbstractRoom> void loadDirections(List<AbstractRoom> rooms,
            Function<T, String> directionIdGetter,
            BiConsumer<T, AbstractRoom> directionSetter, Class<T> clazz) {
        Rooms.listCheckedRooms(clazz, rooms)
                .stream()
                .filter(room -> directionIdGetter.apply(room) != null)
                .forEach(room -> {
                    for (AbstractRoom linkedRoom : rooms) {
                        if (directionIdGetter.apply(room).equals(linkedRoom.getId())) {
                            directionSetter.accept(room, linkedRoom);
                            return;
                        }
                    }

                    throw new Error(
                            "Couldn't link the room (" + room.getId()
                                    + ") directions. Check the JSON file for correct room directions IDs.\nRemember to not use ID: 0");
                });
    }
}
