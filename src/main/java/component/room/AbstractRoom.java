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

    /**
     * Appends a character to the room's ID.
     * 
     * @param value the character you want to append.
     */
    protected void setSecondaryId(char value) {
        setId(getId() + Character.toString(value));
    }
}
