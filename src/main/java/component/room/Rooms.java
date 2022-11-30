package component.room;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import utility.Utils;

public class Rooms {
    private Rooms() {}

    public static List<AbstractRoom> getAllRooms(AbstractRoom room) {
        List<AbstractRoom> result = new ArrayList<>();

        result.add(room);

        if (room instanceof MutableRoom) {
            MutableRoom mRoom = (MutableRoom) room;
            result.addAll(mRoom.getAllRooms());
        }

        return result;
    }

    public static List<AbstractRoom> listAllRooms(List<AbstractRoom> rooms) {
        List<AbstractRoom> result = new ArrayList<>();

        for (AbstractRoom room : rooms) {
            result.addAll(Rooms.getAllRooms(room));
        }

        return result;
    }

    public static <T extends AbstractRoom> List<T> listCheckedRooms(Class<T> clazz,
            List<AbstractRoom> list) {
        return Utils.listCheckedObjects(clazz, list);
    }

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
