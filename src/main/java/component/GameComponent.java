package component;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/** Base class of all the game components (rooms and objects). */
public abstract class GameComponent {
    private String id;
    private String name;
    private String description;

    protected GameComponent(ResultSet resultSet) throws SQLException {
        id = resultSet.getString(1);
        name = resultSet.getString(2);
        description = resultSet.getString(3);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public abstract void saveOnDB() throws SQLException;

    /**
     * Sets the class specific variables on the statement.
     * 
     * @param stm the statement you want to save the object on.
     * @throws SQLException
     */
    protected void setKnownValuesOnStatement(PreparedStatement stm) throws SQLException {
        stm.setString(1, getId());
        stm.setString(2, getName());
        stm.setString(3, getDescription());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        GameComponent other = (GameComponent) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (description == null) {
            if (other.description != null)
                return false;
        } else if (!description.equals(other.description))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "(" + getClass().getSimpleName() + " [id=" + id + ", name=" + name + "])";
    }

}
