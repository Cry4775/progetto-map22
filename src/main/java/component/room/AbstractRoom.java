package component.room;

import java.sql.ResultSet;
import java.sql.SQLException;
import component.GameComponent;

public abstract class AbstractRoom extends GameComponent {

    private String imgPath;

    protected AbstractRoom(ResultSet resultSet) throws SQLException {
        super(resultSet);
        imgPath = resultSet.getString(4);
    }

    public String getImgPath() {
        return imgPath;
    }

    protected void setSecondaryId(char value) {
        setId(getId() + Character.toString(value));
    }
}
