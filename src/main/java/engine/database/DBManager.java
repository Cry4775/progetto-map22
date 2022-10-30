package engine.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import component.entity.AbstractEntity;
import component.room.AbstractRoom;
import engine.loader.RoomsLoader;

public class DBManager {
    private static Connection connection;

    private static final String DB_PATH = "jdbc:h2:./savedata";

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

                createStatement = connection.prepareStatement("CREATE TABLE SAVEDATA.PlayableRoom"
                        + "("
                        + " id varchar(10),"
                        + " mutable boolean,"
                        + " nextRoomId varchar(10),"
                        + " southId varchar(10),"
                        + " northId varchar(10),"
                        + " southWestId varchar(10),"
                        + " northWestId varchar(10),"
                        + " southEastId varchar(10),"
                        + " northEastId varchar(10),"
                        + " eastId varchar(10),"
                        + " westId varchar(10),"
                        + " upId varchar(10),"
                        + " downId varchar(10),"
                        + " currentlyDark boolean"
                        + ")");
                createStatement.executeUpdate();

                createStatement = connection.prepareStatement("CREATE TABLE SAVEDATA.BasicItem"
                        + "("
                        + " id varchar(10),"
                        + " pickedUp boolean,"
                        + " locatedInRoomId varchar(10),"
                        + " locatedInContainerId varchar(10)"
                        + ")");
                createStatement.executeUpdate();

                createStatement = connection.prepareStatement("CREATE TABLE SAVEDATA.FillableItem"
                        + "("
                        + " id varchar(10),"
                        + " pickedUp boolean,"
                        + " locatedInRoomId varchar(10),"
                        + " locatedInContainerId varchar(10),"
                        + " filled boolean"
                        + ")");
                createStatement.executeUpdate();

                createStatement =
                        connection.prepareStatement("CREATE TABLE SAVEDATA.LightSourceItem"
                                + "("
                                + " id varchar(10),"
                                + " pickedUp boolean,"
                                + " locatedInRoomId varchar(10),"
                                + " locatedInContainerId varchar(10),"
                                + " lit boolean"
                                + ")");
                createStatement.executeUpdate();

                createStatement = connection.prepareStatement("CREATE TABLE SAVEDATA.WearableItem"
                        + "("
                        + " id varchar(10),"
                        + " pickedUp boolean,"
                        + " locatedInRoomId varchar(10),"
                        + " locatedInContainerId varchar(10),"
                        + " worn boolean"
                        + ")");
                createStatement.executeUpdate();

                createStatement = connection.prepareStatement("CREATE TABLE SAVEDATA.FireObject"
                        + "("
                        + " id varchar(10),"
                        + " locatedInRoomId varchar(10),"
                        + " locatedInContainerId varchar(10),"
                        + " lit boolean"
                        + ")");
                createStatement.executeUpdate();

                createStatement = connection.prepareStatement("CREATE TABLE SAVEDATA.MovableObject"
                        + "("
                        + " id varchar(10),"
                        + " locatedInRoomId varchar(10),"
                        + " locatedInContainerId varchar(10),"
                        + " moved boolean"
                        + ")");
                createStatement.executeUpdate();

                createStatement = connection.prepareStatement("CREATE TABLE SAVEDATA.PullableObject"
                        + "("
                        + " id varchar(10),"
                        + " locatedInRoomId varchar(10),"
                        + " locatedInContainerId varchar(10),"
                        + " pulled boolean"
                        + ")");
                createStatement.executeUpdate();

                createStatement = connection.prepareStatement("CREATE TABLE SAVEDATA.PushableObject"
                        + "("
                        + " id varchar(10),"
                        + " locatedInRoomId varchar(10),"
                        + " locatedInContainerId varchar(10),"
                        + " pushed boolean"
                        + ")");
                createStatement.executeUpdate();

                createStatement = connection.prepareStatement("CREATE TABLE SAVEDATA.Door"
                        + "("
                        + " id varchar(10),"
                        + " locatedInRoomId varchar(10),"
                        + " open boolean,"
                        + " locked boolean"
                        + ")");
                createStatement.executeUpdate();

                createStatement = connection.prepareStatement("CREATE TABLE SAVEDATA.InvisibleWall"
                        + "("
                        + " id varchar(10),"
                        + " locatedInRoomId varchar(10),"
                        + " locked boolean"
                        + ")");
                createStatement.executeUpdate();

                createStatement =
                        connection.prepareStatement("CREATE TABLE SAVEDATA.ChestlikeContainer"
                                + "("
                                + " id varchar(10),"
                                + " locatedInRoomId varchar(10),"
                                + " locatedInContainerId varchar(10),"
                                + " open boolean,"
                                + " locked boolean"
                                + ")");
                createStatement.executeUpdate();

                createStatement =
                        connection.prepareStatement("CREATE TABLE SAVEDATA.SocketlikeContainer"
                                + "("
                                + " id varchar(10),"
                                + " locatedInRoomId varchar(10),"
                                + " locatedInContainerId varchar(10),"
                                + " itemInside boolean"
                                + ")");
                createStatement.executeUpdate();

                createStatement =
                        connection.prepareStatement("CREATE TABLE SAVEDATA.WearableContainer"
                                + "("
                                + " id varchar(10),"
                                + " pickedUp boolean,"
                                + " locatedInRoomId varchar(10),"
                                + " locatedInContainerId varchar(10),"
                                + " worn boolean"
                                + ")");
                createStatement.executeUpdate();

                createStatement = connection.prepareStatement("CREATE TABLE SAVEDATA.RoomEvent"
                        + "("
                        + " roomId varchar(10),"
                        + " type varchar(128),"
                        + " text varchar(8192)"
                        + ")");
                createStatement.executeUpdate();

                createStatement = connection.prepareStatement("CREATE TABLE SAVEDATA.ObjectEvent"
                        + "("
                        + " objId varchar(10),"
                        + " type varchar(128),"
                        + " text varchar(8192)"
                        + ")");
                createStatement.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void saveRooms() throws SQLException {
        for (AbstractRoom room : RoomsLoader.listAllRooms()) {
            room.saveOnDB(connection);
        }
    }

    public static void saveObjects() throws SQLException {
        for (AbstractEntity obj : RoomsLoader.mapAllObjects().values()) {
            obj.saveOnDB(connection);
        }
    }
}
