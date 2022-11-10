package component.room;

import java.util.ArrayList;
import java.util.List;

public class Rooms {
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
}
