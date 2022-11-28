package engine.loader;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import component.room.AbstractRoom;
import component.room.Rooms;

public class RoomsDirectionSetter<T extends AbstractRoom> implements Runnable {
    List<AbstractRoom> rooms;
    Function<T, String> directionIdGetter;
    BiConsumer<T, AbstractRoom> directionSetter;
    Class<T> clazz;

    public RoomsDirectionSetter(List<AbstractRoom> rooms, Function<T, String> directionIdGetter,
            BiConsumer<T, AbstractRoom> directionSetter, Class<T> clazz) {
        this.rooms = rooms;
        this.directionIdGetter = directionIdGetter;
        this.directionSetter = directionSetter;
        this.clazz = clazz;
    }

    @Override
    public void run() {
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
