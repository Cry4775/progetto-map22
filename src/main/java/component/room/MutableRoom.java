package component.room;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import component.entity.AbstractEntity;
import component.entity.container.AbstractContainer;
import engine.database.DBManager;
import utility.Utils;

public class MutableRoom extends PlayableRoom {

    private MutableRoom newRoom;

    public MutableRoom(ResultSet resultSet) throws SQLException {
        super(resultSet);
    }

    public List<AbstractRoom> getAllRooms() {
        List<AbstractRoom> result = new ArrayList<>();

        if (newRoom != null) {
            result.addAll(Rooms.getAllRooms(newRoom));
        }

        for (int i = 0; i < result.size(); i++) {
            result.get(i).setId(getId());
            result.get(i).setSecondaryId(Character.forDigit(i + 10, 16));
        }

        return result;
    }

    private void updateFields(AbstractRoom newRoom) {
        for (Field f : Utils.getInheritedPrivateFields(newRoom.getClass())) {
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

                        for (AbstractEntity obj : tempList) {
                            obj.setClosestRoomParentId(getId());
                            obj.setClosestRoomParent(this);

                            if (obj instanceof AbstractContainer) {
                                for (AbstractEntity insideObj : AbstractContainer.getAllObjectsInside(obj)) {
                                    insideObj.setClosestRoomParentId(getId());
                                    insideObj.setClosestRoomParent(this);
                                }
                            }
                        }
                    } else {
                        f.set(this, Utils.getField(newRoom.getClass(), f.getName()).get(newRoom));
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

    @Override
    public void saveOnDB() throws SQLException {
        PreparedStatement stm = DBManager.getConnection().prepareStatement(
                "INSERT INTO SAVEDATA.PlayableRoom values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        PreparedStatement evtStm = DBManager.getConnection().prepareStatement(
                "INSERT INTO SAVEDATA.RoomEvent values (?, ?, ?)");

        super.setKnownValuesOnStatement(stm);
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

    public MutableRoom getNewRoom() {
        return newRoom;
    }

    public void setNewRoom(MutableRoom newRoom) {
        this.newRoom = newRoom;
    }

}
