package di.uniba.map.b.adventure.type;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class MutablePlayableRoom extends PlayableRoom {

    private PlayableRoom newRoom;

    public MutablePlayableRoom(int id, String name, String description) {
        super(id, name, description);
    }

    public MutablePlayableRoom(int id, String name, String description, String imgPath) {
        super(id, name, description, imgPath);
    }

    public PlayableRoom getNewRoom() {
        return newRoom;
    }

    public void setNewRoom(PlayableRoom newRoom) {
        this.newRoom = newRoom;
    }

    private void updateFields(Room newRoom) {
        for (Field f : getInheritedPrivateFields(newRoom.getClass())) {
            try {
                if (f.get(newRoom) != null
                        && !f.getName().equals("id")) {
                    if (f.get(newRoom) instanceof ArrayList<?>) {
                        Iterator<AdvObject> it = getObjects().iterator();
                        List<AdvObject> tempList = new ArrayList<>();
                        while (it.hasNext()) {
                            AdvObject obj = it.next();

                            Iterator<?> newIt = ((List<?>) f.get(newRoom)).iterator();
                            while (newIt.hasNext()) {
                                Object newRoomObj = newIt.next();

                                if (newRoomObj instanceof AdvObject) {
                                    if (((AdvObject) newRoomObj).getId() == obj.getId()) {
                                        it.remove();
                                        tempList.add((AdvObject) newRoomObj);
                                        newIt.remove();
                                    }
                                }
                            }
                        }
                        for (Object newRoomObj : (List<?>) f.get(newRoom)) {
                            if (newRoomObj instanceof AdvObject) {
                                tempList.add((AdvObject) newRoomObj);
                            }
                        }
                        getObjects().addAll(tempList);
                    } else {
                        f.set(this, getField(newRoom.getClass(), f.getName()).get(newRoom));
                    }
                }
            } catch (IllegalArgumentException | IllegalAccessException
                    | SecurityException e) {
                e.printStackTrace();
            }
        }

    }

    public void updateToNewRoom() {
        if (newRoom != null) {
            updateFields(newRoom);
        }
    }

    private static Field getField(Class<?> clazz, String fieldName) {
        Class<?> tmpClass = clazz;
        do {
            try {
                Field f = tmpClass.getDeclaredField(fieldName);
                f.setAccessible(true);
                return f;
            } catch (NoSuchFieldException e) {
                tmpClass = tmpClass.getSuperclass();
            }
        } while (tmpClass != null);

        throw new RuntimeException("Field '" + fieldName
                + "' not found on class " + clazz);
    }

    private List<Field> getInheritedPrivateFields(Class<?> type) {
        List<Field> result = new ArrayList<Field>();

        Class<?> i = type;
        while (i != null && i != Object.class) {
            Collections.addAll(result, i.getDeclaredFields());
            i = i.getSuperclass();
        }
        for (Field field : result) {
            field.setAccessible(true);
        }
        return result;
    }

}
