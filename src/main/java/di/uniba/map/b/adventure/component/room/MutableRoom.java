package di.uniba.map.b.adventure.component.room;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import di.uniba.map.b.adventure.component.entity.AbstractEntity;

public class MutableRoom extends PlayableRoom {

    private MutableRoom newRoom;

    public MutableRoom(int id, String name, String description) {
        super(id, name, description);
    }

    public MutableRoom(int id, String name, String description, String imgPath) {
        super(id, name, description, imgPath);
    }

    public MutableRoom getNewRoom() {
        return newRoom;
    }

    public void setNewRoom(MutableRoom newRoom) {
        this.newRoom = newRoom;
    }

    public List<AbstractEntity> getAllObjects() {
        List<AbstractEntity> result = new ArrayList<>();

        for (AbstractRoom room : getAllRooms()) {
            if (room instanceof PlayableRoom) {
                PlayableRoom pRoom = (PlayableRoom) room;

                if (pRoom.getObjects() != null) {
                    result.addAll(pRoom.getObjects());
                }
            }
        }

        result.addAll(getObjects());

        return result;
    }

    public List<AbstractRoom> getAllRooms() {
        List<AbstractRoom> result = new ArrayList<>();

        if (newRoom != null) {
            result.addAll(getAllRooms(newRoom));
        }

        return result;
    }

    private List<AbstractRoom> getAllRooms(AbstractRoom room) {
        List<AbstractRoom> result = new ArrayList<>();

        if (room instanceof MutableRoom) {
            MutableRoom mRoom = (MutableRoom) room;

            if (mRoom.getNewRoom() != null) {
                result.addAll(getAllRooms(mRoom.getNewRoom()));
            }
        }

        result.add(room);
        return result;
    }

    private void updateFields(AbstractRoom newRoom) {
        for (Field f : getInheritedPrivateFields(newRoom.getClass())) {
            try {
                if (f.get(newRoom) != null
                        && !f.getName().equals("id")) {
                    if (f.get(newRoom) instanceof ArrayList<?>) {
                        Iterator<AbstractEntity> it = getObjects().iterator();
                        List<AbstractEntity> tempList = new ArrayList<>();
                        while (it.hasNext()) {
                            AbstractEntity obj = it.next();

                            Iterator<?> newIt = ((List<?>) f.get(newRoom)).iterator();
                            while (newIt.hasNext()) {
                                Object newRoomObj = newIt.next();

                                if (newRoomObj instanceof AbstractEntity) {
                                    if (((AbstractEntity) newRoomObj).getId() == obj.getId()) {
                                        it.remove();
                                        tempList.add((AbstractEntity) newRoomObj);
                                        newIt.remove();
                                    }
                                }
                            }
                        }
                        for (Object newRoomObj : (List<?>) f.get(newRoom)) {
                            if (newRoomObj instanceof AbstractEntity) {
                                tempList.add((AbstractEntity) newRoomObj);
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
