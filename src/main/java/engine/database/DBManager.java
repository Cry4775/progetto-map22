package engine.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import component.entity.AbstractEntity;
import component.entity.container.AbstractContainer;
import component.entity.container.BasicContainer;
import component.entity.container.ChestlikeContainer;
import component.entity.container.SocketlikeContainer;
import component.entity.container.pickupable.WearableContainer;
import component.entity.doorlike.Door;
import component.entity.doorlike.InvisibleWall;
import component.entity.doorlike.UnopenableDoor;
import component.entity.humanoid.Human;
import component.entity.object.BasicObject;
import component.entity.object.FireObject;
import component.entity.object.MovableObject;
import component.entity.object.PullableObject;
import component.entity.object.PushableObject;
import component.entity.pickupable.BasicItem;
import component.entity.pickupable.FillableItem;
import component.entity.pickupable.FluidItem;
import component.entity.pickupable.LightSourceItem;
import component.entity.pickupable.ReadableItem;
import component.entity.pickupable.WearableItem;
import component.event.RoomEvent;
import component.room.AbstractRoom;
import component.room.CutsceneRoom;
import component.room.MutableRoom;
import component.room.PlayableRoom;
import component.room.PlayableRoom.Mode;
import engine.GameManager;
import engine.Inventory;
import gui.GUIManager;
import component.room.Rooms;
import utility.Triple;

public class DBManager {
    private static Connection connection;

    private static final String DB_PATH = "jdbc:h2:./savedata";

    static List<AbstractRoom> loadedRooms = new ArrayList<>();

    public static Connection getConnection() {
        return connection;
    }

    private static void openConnection() throws SQLException {
        if (connection == null || !connection.isValid(0)) {
            connection = DriverManager.getConnection(DB_PATH, "user", "");
        }
    }

    private static void closeConnection() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            throw new Error(
                    "An error has occurred while attempting to close connection with DB. Details: "
                            + e.getMessage());
        }
    }

    private static boolean existsDB() {
        try {

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
        } catch (SQLException e) {
            closeConnection();
            throw new Error(
                    "An error has occurred while attempting to fetch DB existence. Details: "
                            + e.getMessage());
        }
    }

    public static void createDB() {
        try {
            openConnection();

            if (!existsDB()) {
                PreparedStatement createStatement =
                        connection.prepareStatement("CREATE SCHEMA SAVEDATA");
                createStatement.executeUpdate();

                createStatement = connection.prepareStatement("CREATE TABLE SAVEDATA.CurrentRoom"
                        + "("
                        + " id varchar(10),"
                        + " previousRoomId varchar(10)"
                        + ")");
                createStatement.executeUpdate();

                createStatement = connection.prepareStatement("CREATE TABLE SAVEDATA.CutsceneRoom"
                        + "("
                        + " id varchar(10),"
                        + " name varchar(128),"
                        + " description varchar(8192),"
                        + " imgPath varchar(1024),"
                        + " nextRoomId varchar(10),"
                        + " finalRoom boolean"
                        + ")");
                createStatement.executeUpdate();

                createStatement = connection.prepareStatement("CREATE TABLE SAVEDATA.PlayableRoom"
                        + "("
                        + " id varchar(10),"
                        + " name varchar(128),"
                        + " description varchar(8192),"
                        + " imgPath varchar(1024),"
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
                        + " darkByDefault boolean"
                        + ")");
                createStatement.executeUpdate();

                createStatement =
                        connection.prepareStatement("CREATE TABLE SAVEDATA.RequiredWearedItem"
                                + "("
                                + " id varchar(10),"
                                + " requiredWearedItemId varchar(10),"
                                + " failedInteractionMessage varchar(512)"
                                + ")");
                createStatement.executeUpdate();

                createStatement = connection.prepareStatement("CREATE TABLE SAVEDATA.BasicItem"
                        + "("
                        + " id varchar(10),"
                        + " name varchar(128),"
                        + " description varchar(8192),"
                        + " locatedInRoomId varchar(10),"
                        + " locatedInContainerId varchar(10),"
                        + " pickedUp boolean"
                        + ")");
                createStatement.executeUpdate();

                createStatement = connection.prepareStatement("CREATE TABLE SAVEDATA.FillableItem"
                        + "("
                        + " id varchar(10),"
                        + " name varchar(128),"
                        + " description varchar(8192),"
                        + " locatedInRoomId varchar(10),"
                        + " locatedInContainerId varchar(10),"
                        + " pickedUp boolean,"
                        + " filled boolean,"
                        + " eligibleItemId varchar(10)"
                        + ")");
                createStatement.executeUpdate();

                createStatement =
                        connection.prepareStatement("CREATE TABLE SAVEDATA.LightSourceItem"
                                + "("
                                + " id varchar(10),"
                                + " name varchar(128),"
                                + " description varchar(8192),"
                                + " locatedInRoomId varchar(10),"
                                + " locatedInContainerId varchar(10),"
                                + " pickedUp boolean,"
                                + " lit boolean,"
                                + " requiredItemId varchar(10)"
                                + ")");
                createStatement.executeUpdate();

                createStatement = connection.prepareStatement("CREATE TABLE SAVEDATA.WearableItem"
                        + "("
                        + " id varchar(10),"
                        + " name varchar(128),"
                        + " description varchar(8192),"
                        + " locatedInRoomId varchar(10),"
                        + " locatedInContainerId varchar(10),"
                        + " pickedUp boolean,"
                        + " worn boolean"
                        + ")");
                createStatement.executeUpdate();

                createStatement = connection.prepareStatement("CREATE TABLE SAVEDATA.FireObject"
                        + "("
                        + " id varchar(10),"
                        + " name varchar(128),"
                        + " description varchar(8192),"
                        + " locatedInRoomId varchar(10),"
                        + " locatedInContainerId varchar(10),"
                        + " lit boolean"
                        + ")");
                createStatement.executeUpdate();

                createStatement = connection.prepareStatement("CREATE TABLE SAVEDATA.MovableObject"
                        + "("
                        + " id varchar(10),"
                        + " name varchar(128),"
                        + " description varchar(8192),"
                        + " locatedInRoomId varchar(10),"
                        + " locatedInContainerId varchar(10),"
                        + " moved boolean"
                        + ")");
                createStatement.executeUpdate();

                createStatement = connection.prepareStatement("CREATE TABLE SAVEDATA.PullableObject"
                        + "("
                        + " id varchar(10),"
                        + " name varchar(128),"
                        + " description varchar(8192),"
                        + " locatedInRoomId varchar(10),"
                        + " locatedInContainerId varchar(10),"
                        + " pulled boolean"
                        + ")");
                createStatement.executeUpdate();

                createStatement = connection.prepareStatement("CREATE TABLE SAVEDATA.PushableObject"
                        + "("
                        + " id varchar(10),"
                        + " name varchar(128),"
                        + " description varchar(8192),"
                        + " locatedInRoomId varchar(10),"
                        + " locatedInContainerId varchar(10),"
                        + " pushed boolean"
                        + ")");
                createStatement.executeUpdate();

                createStatement = connection.prepareStatement("CREATE TABLE SAVEDATA.Door"
                        + "("
                        + " id varchar(10),"
                        + " name varchar(128),"
                        + " description varchar(8192),"
                        + " locatedInRoomId varchar(10),"
                        + " locatedInContainerId varchar(10),"
                        + " open boolean,"
                        + " locked boolean,"
                        + " unlockedWithItemId varchar(10),"
                        + " blockedRoomId varchar(10)"
                        + ")");
                createStatement.executeUpdate();

                createStatement = connection.prepareStatement("CREATE TABLE SAVEDATA.InvisibleWall"
                        + "("
                        + " id varchar(10),"
                        + " name varchar(128),"
                        + " description varchar(8192),"
                        + " locatedInRoomId varchar(10),"
                        + " locatedInContainerId varchar(10),"
                        + " locked boolean,"
                        + " blockedRoomId varchar(10),"
                        + " trespassingWhenLockedText varchar(8192),"
                        + " northBlocked boolean,"
                        + " southBlocked boolean,"
                        + " eastBlocked boolean,"
                        + " westBlocked boolean,"
                        + " northEastBlocked boolean,"
                        + " northWestBlocked boolean,"
                        + " southEastBlocked boolean,"
                        + " southWestBlocked boolean,"
                        + " upBlocked boolean,"
                        + " downBlocked boolean"
                        + ")");
                createStatement.executeUpdate();

                createStatement =
                        connection.prepareStatement("CREATE TABLE SAVEDATA.ChestlikeContainer"
                                + "("
                                + " id varchar(10),"
                                + " name varchar(128),"
                                + " description varchar(8192),"
                                + " locatedInRoomId varchar(10),"
                                + " locatedInContainerId varchar(10),"
                                + " open boolean,"
                                + " locked boolean,"
                                + " unlockedWithItemId varchar(10),"
                                + " forFluids boolean"
                                + ")");
                createStatement.executeUpdate();

                createStatement =
                        connection.prepareStatement("CREATE TABLE SAVEDATA.SocketlikeContainer"
                                + "("
                                + " id varchar(10),"
                                + " name varchar(128),"
                                + " description varchar(8192),"
                                + " locatedInRoomId varchar(10),"
                                + " locatedInContainerId varchar(10),"
                                + " itemInside boolean,"
                                + " eligibleItemId varchar(10),"
                                + " forFluids boolean"
                                + ")");
                createStatement.executeUpdate();

                createStatement =
                        connection.prepareStatement("CREATE TABLE SAVEDATA.WearableContainer"
                                + "("
                                + " id varchar(10),"
                                + " name varchar(128),"
                                + " description varchar(8192),"
                                + " locatedInRoomId varchar(10),"
                                + " locatedInContainerId varchar(10),"
                                + " pickedUp boolean,"
                                + " worn boolean,"
                                + " maxSlots int,"
                                + " forFluids boolean"
                                + ")");
                createStatement.executeUpdate();

                createStatement =
                        connection.prepareStatement("CREATE TABLE SAVEDATA.BasicContainer"
                                + "("
                                + " id varchar(10),"
                                + " name varchar(128),"
                                + " description varchar(8192),"
                                + " locatedInRoomId varchar(10),"
                                + " locatedInContainerId varchar(10),"
                                + " forFluids boolean"
                                + ")");
                createStatement.executeUpdate();

                createStatement =
                        connection.prepareStatement("CREATE TABLE SAVEDATA.ReadableItem"
                                + "("
                                + " id varchar(10),"
                                + " name varchar(128),"
                                + " description varchar(8192),"
                                + " locatedInRoomId varchar(10),"
                                + " locatedInContainerId varchar(10),"
                                + " pickedUp boolean,"
                                + " text varchar(8192)"
                                + ")");
                createStatement.executeUpdate();

                createStatement =
                        connection.prepareStatement("CREATE TABLE SAVEDATA.FluidItem"
                                + "("
                                + " id varchar(10),"
                                + " name varchar(128),"
                                + " description varchar(8192),"
                                + " locatedInRoomId varchar(10),"
                                + " locatedInContainerId varchar(10),"
                                + " pickedUp boolean"
                                + ")");
                createStatement.executeUpdate();

                createStatement =
                        connection.prepareStatement("CREATE TABLE SAVEDATA.BasicObject"
                                + "("
                                + " id varchar(10),"
                                + " name varchar(128),"
                                + " description varchar(8192),"
                                + " locatedInRoomId varchar(10),"
                                + " locatedInContainerId varchar(10)"
                                + ")");
                createStatement.executeUpdate();

                createStatement =
                        connection.prepareStatement("CREATE TABLE SAVEDATA.UnopenableDoor"
                                + "("
                                + " id varchar(10),"
                                + " name varchar(128),"
                                + " description varchar(8192),"
                                + " locatedInRoomId varchar(10),"
                                + " locatedInContainerId varchar(10),"
                                + " openEventText varchar(8192)"
                                + ")");
                createStatement.executeUpdate();

                createStatement =
                        connection.prepareStatement("CREATE TABLE SAVEDATA.Human"
                                + "("
                                + " id varchar(10),"
                                + " name varchar(128),"
                                + " description varchar(8192),"
                                + " locatedInRoomId varchar(10),"
                                + " locatedInContainerId varchar(10)"
                                + ")");
                createStatement.executeUpdate();

                createStatement =
                        connection.prepareStatement("CREATE TABLE SAVEDATA.HumanPhrases"
                                + "("
                                + " id varchar(10),"
                                + " text varchar(8192)"
                                + ")");
                createStatement.executeUpdate();

                createStatement = connection.prepareStatement("CREATE TABLE SAVEDATA.Alias"
                        + "("
                        + " id varchar(10),"
                        + " alias varchar(512)"
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
                        + " roomId varchar(10),"
                        + " type varchar(128),"
                        + " text varchar(8192),"
                        + " updatingParentRoom boolean,"
                        + " updateTargetRoomId varchar(10),"
                        + " teleportsPlayerToRoomId varchar(10),"
                        + " destroyOnTrigger boolean"
                        + ")");
                createStatement.executeUpdate();

                createStatement = connection.prepareStatement("CREATE TABLE SAVEDATA.ActionsCounter"
                        + "("
                        + " currentValue int"
                        + ")");
                createStatement.executeUpdate();
            }

        } catch (SQLException e) {
            closeConnection();
            throw new Error(
                    "An error has occurred while attempting to create DB. Details: "
                            + e.getMessage());
        }
    }

    public static void saveRooms(GameManager gameManager) throws SQLException {
        for (AbstractRoom room : gameManager.listAllRooms()) {
            room.saveOnDB();
        }

        PreparedStatement stm = connection.prepareStatement(
                "INSERT INTO SAVEDATA.CurrentRoom values (?, ?)");

        stm.setString(1, gameManager.getCurrentRoom().getId());
        stm.setString(2, gameManager.getPreviousRoom() != null
                ? gameManager.getPreviousRoom().getId()
                : null);
        stm.executeUpdate();
    }

    public static void saveObjects(GameManager gameManager) throws SQLException {
        for (AbstractEntity obj : gameManager.mapAllRoomsObjects().values()) {
            obj.saveOnDB();
        }

        List<AbstractEntity> inventory = gameManager.getInventory().getObjects(Inventory.Mode.UNPACK_CONTAINERS);

        for (AbstractEntity obj : inventory) {
            obj.saveOnDB();
        }
    }

    public static void wipeExistingDB() throws SQLException {
        PreparedStatement stm;

        stm = connection.prepareStatement("DELETE FROM SAVEDATA.CurrentRoom");
        stm.executeUpdate();

        stm = connection.prepareStatement("DELETE FROM SAVEDATA.Alias");
        stm.executeUpdate();

        stm = connection.prepareStatement("DELETE FROM SAVEDATA.BasicContainer");
        stm.executeUpdate();

        stm = connection.prepareStatement("DELETE FROM SAVEDATA.BasicItem");
        stm.executeUpdate();

        stm = connection.prepareStatement("DELETE FROM SAVEDATA.BasicObject");
        stm.executeUpdate();

        stm = connection.prepareStatement("DELETE FROM SAVEDATA.ChestlikeContainer");
        stm.executeUpdate();

        stm = connection.prepareStatement("DELETE FROM SAVEDATA.CutsceneRoom");
        stm.executeUpdate();

        stm = connection.prepareStatement("DELETE FROM SAVEDATA.Door");
        stm.executeUpdate();

        stm = connection.prepareStatement("DELETE FROM SAVEDATA.FillableItem");
        stm.executeUpdate();

        stm = connection.prepareStatement("DELETE FROM SAVEDATA.FireObject");
        stm.executeUpdate();

        stm = connection.prepareStatement("DELETE FROM SAVEDATA.FluidItem");
        stm.executeUpdate();

        stm = connection.prepareStatement("DELETE FROM SAVEDATA.Human");
        stm.executeUpdate();

        stm = connection.prepareStatement("DELETE FROM SAVEDATA.HumanPhrases");
        stm.executeUpdate();

        stm = connection.prepareStatement("DELETE FROM SAVEDATA.InvisibleWall");
        stm.executeUpdate();

        stm = connection.prepareStatement("DELETE FROM SAVEDATA.LightSourceItem");
        stm.executeUpdate();

        stm = connection.prepareStatement("DELETE FROM SAVEDATA.MovableObject");
        stm.executeUpdate();

        stm = connection.prepareStatement("DELETE FROM SAVEDATA.ObjectEvent");
        stm.executeUpdate();

        stm = connection.prepareStatement("DELETE FROM SAVEDATA.PlayableRoom");
        stm.executeUpdate();

        stm = connection.prepareStatement("DELETE FROM SAVEDATA.PullableObject");
        stm.executeUpdate();

        stm = connection.prepareStatement("DELETE FROM SAVEDATA.PushableObject");
        stm.executeUpdate();

        stm = connection.prepareStatement("DELETE FROM SAVEDATA.ReadableItem");
        stm.executeUpdate();

        stm = connection.prepareStatement("DELETE FROM SAVEDATA.RequiredWearedItem");
        stm.executeUpdate();

        stm = connection.prepareStatement("DELETE FROM SAVEDATA.RoomEvent");
        stm.executeUpdate();

        stm = connection.prepareStatement("DELETE FROM SAVEDATA.SocketlikeContainer");
        stm.executeUpdate();

        stm = connection.prepareStatement("DELETE FROM SAVEDATA.UnopenableDoor");
        stm.executeUpdate();

        stm = connection.prepareStatement("DELETE FROM SAVEDATA.WearableContainer");
        stm.executeUpdate();

        stm = connection.prepareStatement("DELETE FROM SAVEDATA.WearableItem");
        stm.executeUpdate();

        stm = connection.prepareStatement("DELETE FROM SAVEDATA.ActionsCounter");
        stm.executeUpdate();
    }

    public static boolean existsSaving() {
        try {
            openConnection();

            if (!existsDB()) {
                return false;
            }

            PreparedStatement stm =
                    connection.prepareStatement("SELECT id FROM SAVEDATA.PlayableRoom");
            ResultSet resultSet = stm.executeQuery();

            if (resultSet.next()) {
                resultSet.close();
                stm.close();

                return true;
            }

            return false;
        } catch (SQLException e) {
            closeConnection();
            throw new Error(
                    "An error has occurred while attempting to fetch existence of savings on DB. Details: "
                            + e.getMessage());
        }
    }

    public static void save(GameManager gameManager) {
        try {
            openConnection();
            wipeExistingDB();

            saveRooms(gameManager);
            saveObjects(gameManager);

            PreparedStatement stm = connection.prepareStatement(
                    "INSERT INTO SAVEDATA.ActionsCounter values (?)");

            stm.setInt(1, GUIManager.getCurrentActionsCounterValue());
            stm.executeUpdate();
        } catch (SQLException e) {
            closeConnection();
            e.printStackTrace();
            throw new Error(
                    "An error has occurred while attempting to save state on DB. Details: "
                            + e.getMessage());
        }
    }

    public static List<AbstractRoom> load(Inventory inventory) {
        try {
            openConnection();

            loadRooms();
            loadObjects(inventory);

            PreparedStatement stm =
                    connection.prepareStatement("SELECT * FROM SAVEDATA.ActionsCounter");
            ResultSet stmResultSet = stm.executeQuery();

            while (stmResultSet.next()) {
                int value = stmResultSet.getInt(1);

                if (value != 0) {
                    GUIManager.setCurrentActionsCounterValue(value);
                }
            }
            stm.close();

            return loadedRooms;
        } catch (SQLException e) {
            closeConnection();
            e.printStackTrace();
            throw new Error(
                    "An error has occurred while attempting to load state on DB. Details: "
                            + e.getMessage());
        }
    }

    public static String getCurrentRoomId() {
        try {
            PreparedStatement stm =
                    connection.prepareStatement("SELECT * FROM SAVEDATA.CurrentRoom");
            ResultSet resultSet = stm.executeQuery();

            while (resultSet.next()) {
                return resultSet.getString(1);
            }
        } catch (SQLException e) {

        }

        return null;
    }

    public static String getPreviousRoomId() {
        try {
            PreparedStatement stm =
                    connection.prepareStatement("SELECT * FROM SAVEDATA.CurrentRoom");
            ResultSet resultSet = stm.executeQuery();

            while (resultSet.next()) {
                return resultSet.getString(2);
            }
        } catch (SQLException e) {

        }

        return null;
    }

    private static void loadObjects(Inventory inventory) throws SQLException {
        List<Triple<AbstractEntity, String, String>> pendingList = new ArrayList<>();
        List<AbstractRoom> allRooms = Rooms.listAllRooms(loadedRooms);

        BasicContainer.loadFromDB(allRooms, pendingList);
        ChestlikeContainer.loadFromDB(allRooms, pendingList);
        SocketlikeContainer.loadFromDB(allRooms, pendingList);
        WearableContainer.loadFromDB(allRooms, inventory, pendingList);

        for (Triple<AbstractEntity, String, String> pending : pendingList) {
            AbstractEntity object = pending.getFirst();
            String roomId = pending.getSecond();
            String containerId = pending.getThird();

            if (roomId == null && containerId != null) {
                AbstractContainer.addObjectToContainerId(
                        object, inventory.getObjects(Inventory.Mode.UNPACK_CONTAINERS), containerId);
                break;
            }

            for (AbstractRoom room : allRooms) {
                if (room.getId().equals(roomId)) {
                    PlayableRoom pRoom = (PlayableRoom) room;
                    object.setClosestRoomParent(pRoom);

                    if (containerId != null) {
                        AbstractContainer.addObjectToContainerId(
                                object, pRoom.getObjects(Mode.UNPACK_CONTAINERS), containerId);
                        break;
                    }

                    pRoom.getObjects().add(object);
                    break;
                }
            }
        }

        BasicItem.loadFromDB(allRooms, inventory);
        BasicObject.loadFromDB(allRooms, inventory);
        Door.loadFromDB(allRooms, inventory);
        FillableItem.loadFromDB(allRooms, inventory);
        FireObject.loadFromDB(allRooms, inventory);
        FluidItem.loadFromDB(allRooms, inventory);
        Human.loadFromDB(allRooms, inventory);
        InvisibleWall.loadFromDB(allRooms, inventory);
        LightSourceItem.loadFromDB(allRooms, inventory);
        MovableObject.loadFromDB(allRooms, inventory);
        PullableObject.loadFromDB(allRooms, inventory);
        PushableObject.loadFromDB(allRooms, inventory);
        ReadableItem.loadFromDB(allRooms, inventory);
        UnopenableDoor.loadFromDB(allRooms, inventory);
        WearableItem.loadFromDB(allRooms, inventory);
    }

    private static void loadRooms() throws SQLException {
        PreparedStatement stm = connection.prepareStatement("SELECT * FROM SAVEDATA.PlayableRoom");
        ResultSet resultSet = stm.executeQuery();

        while (resultSet.next()) {
            boolean mutable = resultSet.getBoolean(5);

            PlayableRoom room;

            if (mutable) {
                room = new MutableRoom(resultSet);

                insertNewRoom(resultSet, (MutableRoom) room);
            } else {
                room = new PlayableRoom(resultSet);
            }

            PreparedStatement evtStm =
                    connection.prepareStatement("SELECT * FROM SAVEDATA.RoomEvent");
            ResultSet evtResultSet = evtStm.executeQuery();

            while (evtResultSet.next()) {
                String roomId = evtResultSet.getString(1);

                if (room.getId().equals(roomId)) {
                    RoomEvent evt = new RoomEvent(resultSet);

                    room.setEvent(evt);
                }
            }
            loadedRooms.add(room);
            evtStm.close();
            evtResultSet.close();
        }
        stm.close();
        resultSet.close();

        stm = connection.prepareStatement("SELECT * FROM SAVEDATA.CutsceneRoom");
        resultSet = stm.executeQuery();

        while (resultSet.next()) {
            CutsceneRoom room = new CutsceneRoom(resultSet);

            loadedRooms.add(room);
        }
    }

    private static void insertNewRoom(ResultSet resultSet, MutableRoom parentRoom)
            throws SQLException {
        resultSet.next();
        boolean mutable = resultSet.getBoolean(5);

        MutableRoom room = new MutableRoom(resultSet);

        if (mutable) {
            insertNewRoom(resultSet, (MutableRoom) room);
        }

        parentRoom.setNewRoom(room);
    }
}
