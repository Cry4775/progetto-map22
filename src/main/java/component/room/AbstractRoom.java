package component.room;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import component.GameComponent;

public abstract class AbstractRoom extends GameComponent {

    private String imgPath;

    public AbstractRoom(ResultSet resultSet) throws SQLException {
        super(resultSet);
        imgPath = resultSet.getString(4);
    }

    public static List<AbstractRoom> getAllRooms(AbstractRoom room) {
        List<AbstractRoom> result = new ArrayList<>();

        result.add(room);

        if (room instanceof MutableRoom) {
            MutableRoom mRoom = (MutableRoom) room;

            if (mRoom.getNewRoom() != null) {
                result.addAll(getAllRooms(mRoom.getNewRoom()));
            }
        }

        return result;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public void setSecondaryId(char value) {
        setId(getId() + Character.toString(value));
    }
}
