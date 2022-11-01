package component.room;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import component.entity.AbstractEntity;

public class MutableRoom extends PlayableRoom {

    public MutableRoom(ResultSet resultSet) throws SQLException {
        super(resultSet);
    }

    private MutableRoom newRoom;

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

        for (int i = 0; i < result.size(); i++) {
            result.get(i).setId(getId());
            result.get(i).setSecondaryId(Character.forDigit(i + 10, 16));
        }

        return result;
    }

    private List<AbstractRoom> getAllRooms(AbstractRoom room) {
        List<AbstractRoom> result = new ArrayList<>();

        if (room instanceof MutableRoom) {
            MutableRoom mRoom = (MutableRoom) room;

            result.add(room);

            if (mRoom.getNewRoom() != null) {
                result.addAll(getAllRooms(mRoom.getNewRoom()));
            }
        } else {
            result.add(room);
        }

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
                                    if (((AbstractEntity) newRoomObj).getId().equals(obj.getId())) {
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

        // Destroy the "newRoom" if it's there's none later
        if (newRoom instanceof MutableRoom) {
            if (((MutableRoom) newRoom).getNewRoom() == null) {
                setNewRoom(null);
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

        throw new Error("Field '" + fieldName
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

    @Override
    public void saveOnDB(Connection connection) throws SQLException {
        PreparedStatement stm = connection.prepareStatement(
                "INSERT INTO SAVEDATA.PlayableRoom values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        PreparedStatement evtStm = connection.prepareStatement(
                "INSERT INTO SAVEDATA.RoomEvent values (?, ?, ?)");

        stm.setString(1, getId());
        stm.setString(2, getName());
        stm.setString(3, getDescription());
        stm.setString(4, getImgPath());
        stm.setBoolean(5, newRoom != null ? true : false);
        stm.setString(6, newRoom != null ? newRoom.getId() : null);
        stm.setString(7, getSouthId());
        stm.setString(8, getNorthId());
        stm.setString(9, getSouthWestId());
        stm.setString(10, getNorthWestId());
        stm.setString(11, getSouthEastId());
        stm.setString(12, getNorthEastId());
        stm.setString(13, getEastId());
        stm.setString(14, getWestId());
        stm.setString(15, getUpId());
        stm.setString(16, getDownId());
        stm.setBoolean(17, isDarkByDefault());
        stm.executeUpdate();

        if (getEvent() != null) {
            evtStm.setString(1, getId());
            evtStm.setString(2, getEvent().getEventType().toString());
            evtStm.setString(3, getEvent().getText());
            evtStm.executeUpdate();
        }
    }

}
