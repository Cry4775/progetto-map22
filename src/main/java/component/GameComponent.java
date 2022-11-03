package component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class GameComponent {
    private String id;

    private String name;

    private String description;

    public GameComponent(ResultSet resultSet) throws SQLException {
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

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
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
        return true;
    }

    public abstract void saveOnDB(Connection connection) throws SQLException;

    public void setValuesOnStatement(PreparedStatement stm) throws SQLException {
        stm.setString(1, getId());
        stm.setString(2, getName());
        stm.setString(3, getDescription());
    }

}
