package engine.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBManager {
    private static Connection connection;

    private static final String DB_PATH = "jdbc:h2:./savedata";

    private static final String SQL_GAME_COMPONENT = " id varchar(5),"
            + " name varchar(100),"
            + " description varchar(8192),";

    private static final String SQL_ABSTRACT_ROOM = " imgPath varchar(256),";

    private static void openConnection() throws SQLException {
        if (connection == null || !connection.isValid(0)) {
            connection = DriverManager.getConnection(DB_PATH, "user", "");
        }
    }

    private static void closeConnection() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    private static boolean existsDB() throws SQLException {
        openConnection();
        DatabaseMetaData databaseMetaData = connection.getMetaData();
        ResultSet resultSet = databaseMetaData.getSchemas();

        while (resultSet.next()) {
            String name = resultSet.getString("TABLE_SCHEM");

            // DB esistente
            if (name.equalsIgnoreCase("SAVEDATA")) {
                return true;
            }
        }

        return false;
    }

    public static void createDB() {
        try {
            openConnection();

            if (!existsDB()) {
                PreparedStatement createStatement =
                        connection.prepareStatement("CREATE SCHEMA SAVEDATA");
                createStatement.executeUpdate();

                createStatement = connection.prepareStatement("CREATE TABLE SAVEDATA.CutsceneRoom"
                        + "("
                        + SQL_GAME_COMPONENT
                        + SQL_ABSTRACT_ROOM
                        + " nextRoomId int,"
                        + " finalRoom boolean"
                        + ")");
                createStatement.executeUpdate();

                createStatement = connection.prepareStatement("CREATE TABLE SAVEDATA.PlayableRoom"
                        + "("
                        + SQL_GAME_COMPONENT
                        + SQL_ABSTRACT_ROOM
                        + " mutable boolean,"
                        + " southId int,"
                        + " northId int,"
                        + " southWestId int,"
                        + " northWestId int,"
                        + " southEastId int,"
                        + " northEastId int,"
                        + " eastId int,"
                        + " westId int,"
                        + " upId int,"
                        + " downId int,"
                        + " currentlyDark boolean,"
                        + " darkByDefault boolean"
                        + ")");
                createStatement.executeUpdate();

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
