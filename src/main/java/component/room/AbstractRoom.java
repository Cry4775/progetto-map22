package component.room;

import java.sql.ResultSet;
import java.sql.SQLException;
import component.GameComponent;

public abstract class AbstractRoom extends GameComponent {

    public AbstractRoom(ResultSet resultSet) throws SQLException {
        super(resultSet);
        imgPath = resultSet.getString(4);
    }

    private String imgPath;

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
