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
import engine.GameManager;
import engine.loader.RoomsLoader;
import utility.Pair;

public class DBManager {
    private static Connection connection;

    private static final String DB_PATH = "jdbc:h2:./savedata";

    static List<AbstractRoom> rooms = new ArrayList<>();

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
                        + " id varchar(10)"
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
                        + " locked boolean"
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
                        + " type varchar(128),"
                        + " text varchar(8192),"
                        + " updatingParentRoom boolean,"
                        + " updateTargetRoomId varchar(10),"
                        + " teleportsPlayerToRoomId varchar(10),"
                        + " destroyOnTrigger boolean"
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

    public static void saveRooms() throws SQLException {
        for (AbstractRoom room : RoomsLoader.listAllRooms()) {
            room.saveOnDB(connection);
        }

        PreparedStatement stm = connection.prepareStatement(
                "INSERT INTO SAVEDATA.CurrentRoom values (?)");

        stm.setString(1, GameManager.getCurrentRoom().getId());
        stm.executeUpdate();
    }

    public static void saveObjects() throws SQLException {
        for (AbstractEntity obj : RoomsLoader.mapAllObjects().values()) {
            obj.saveOnDB(connection);
        }

        List<AbstractEntity> inventory = new ArrayList<>();

        for (AbstractEntity obj : GameManager.getInventory()) {
            obj.saveOnDB(connection);
            if (obj instanceof AbstractContainer) {
                AbstractContainer container = (AbstractContainer) obj;

                inventory.addAll(container.getAllObjects());
            }
        }

        for (AbstractEntity abstractEntity : inventory) {
            abstractEntity.saveOnDB(connection);
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

    public static void save() {
        try {
            openConnection();
            wipeExistingDB();

            saveRooms();
            saveObjects();
            // TODO counter azioni
        } catch (SQLException e) {
            closeConnection();
            e.printStackTrace();
            throw new Error(
                    "An error has occurred while attempting to save state on DB. Details: "
                            + e.getMessage());
        }
    }

    public static List<AbstractRoom> load() {
        try {
            openConnection();

            loadRooms();
            loadObjects();

            return rooms;
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

    private static void loadObjects() throws SQLException {
        List<Pair<AbstractEntity, String>> pending = new ArrayList<>();

        PreparedStatement stm =
                connection.prepareStatement("SELECT * FROM SAVEDATA.BasicContainer");
        ResultSet resultSet = stm.executeQuery();

        while (resultSet.next()) {
            String roomId = resultSet.getString(4);
            String containerId = resultSet.getString(5);

            BasicContainer obj = new BasicContainer(resultSet);

            if (!roomId.equals("null")) {
                for (AbstractRoom room : RoomsLoader.listAllRooms(rooms)) {
                    if (roomId.equals(room.getId())) {
                        PlayableRoom pRoom = (PlayableRoom) room;
                        pRoom.getObjects().add(obj);
                        break;
                    }
                }
            } else if (!containerId.equals("null")) {
                pending.add(new Pair<AbstractEntity, String>(obj, containerId));
            }
        }

        stm.close();

        stm = connection.prepareStatement("SELECT * FROM SAVEDATA.ChestlikeContainer");
        resultSet = stm.executeQuery();

        while (resultSet.next()) {
            String roomId = resultSet.getString(4);
            String containerId = resultSet.getString(5);

            ChestlikeContainer obj = new ChestlikeContainer(resultSet);

            if (!roomId.equals("null")) {
                for (AbstractRoom room : RoomsLoader.listAllRooms(rooms)) {
                    if (roomId.equals(room.getId())) {
                        PlayableRoom pRoom = (PlayableRoom) room;
                        pRoom.getObjects().add(obj);
                        break;
                    }
                }
            } else if (!containerId.equals("null")) {
                pending.add(new Pair<AbstractEntity, String>(obj, containerId));
            }
        }

        stm.close();

        stm = connection.prepareStatement("SELECT * FROM SAVEDATA.SocketlikeContainer");
        resultSet = stm.executeQuery();

        while (resultSet.next()) {
            String roomId = resultSet.getString(4);
            String containerId = resultSet.getString(5);

            SocketlikeContainer obj = new SocketlikeContainer(resultSet);

            if (!roomId.equals("null")) {
                for (AbstractRoom room : RoomsLoader.listAllRooms(rooms)) {
                    if (roomId.equals(room.getId())) {
                        PlayableRoom pRoom = (PlayableRoom) room;
                        pRoom.getObjects().add(obj);
                        break;
                    }
                }
            } else if (!containerId.equals("null")) {
                pending.add(new Pair<AbstractEntity, String>(obj, containerId));
            }
        }

        stm.close();

        stm = connection.prepareStatement("SELECT * FROM SAVEDATA.WearableContainer");
        resultSet = stm.executeQuery();

        while (resultSet.next()) {
            String roomId = resultSet.getString(4);
            String containerId = resultSet.getString(5);

            WearableContainer obj = new WearableContainer(resultSet);

            if (obj.isPickedUp()) {
                GameManager.getInventory().add(obj);
            } else if (!roomId.equals("null")) {
                for (AbstractRoom room : RoomsLoader.listAllRooms(rooms)) {
                    if (roomId.equals(room.getId())) {
                        PlayableRoom pRoom = (PlayableRoom) room;
                        pRoom.getObjects().add(obj);
                        break;
                    }
                }
            } else if (!containerId.equals("null")) {
                pending.add(new Pair<AbstractEntity, String>(obj, containerId));
            }
        }

        stm.close();
        boolean found = false;

        for (Pair<AbstractEntity, String> pendingItem : pending) {
            found = false;
            for (AbstractEntity entity : RoomsLoader.mapAllObjects(rooms).values()) {
                if (entity instanceof AbstractContainer
                        && pendingItem.getSecond().equals(entity.getId())) {
                    AbstractContainer aContainer = (AbstractContainer) entity;

                    aContainer.add(pendingItem.getFirst());
                    found = true;
                    break;
                }
            }

            if (!found) {
                for (AbstractEntity obj : GameManager.getFullInventory()) {
                    if (obj instanceof AbstractContainer
                            && pendingItem.getSecond().equals(obj.getId())) {
                        AbstractContainer aContainer = (AbstractContainer) obj;

                        aContainer.add(pendingItem.getFirst());
                        break;
                    }
                }
            }
        }

        stm = connection.prepareStatement("SELECT * FROM SAVEDATA.BasicItem");
        resultSet = stm.executeQuery();

        while (resultSet.next()) {
            found = false;
            String roomId = resultSet.getString(4);
            String containerId = resultSet.getString(5);

            BasicItem basicItem = new BasicItem(resultSet);

            if (basicItem.isPickedUp()) {
                GameManager.getInventory().add(basicItem);
            } else if (!roomId.equals("null")) {
                for (AbstractRoom room : RoomsLoader.listAllRooms(rooms)) {
                    if (roomId.equals(room.getId())) {
                        PlayableRoom pRoom = (PlayableRoom) room;
                        pRoom.getObjects().add(basicItem);
                        break;
                    }
                }
            } else if (!containerId.equals("null")) {
                for (AbstractEntity obj : RoomsLoader.mapAllObjects(rooms).values()) {
                    if (obj instanceof AbstractContainer && containerId.equals(obj.getId())) {
                        AbstractContainer aContainer = (AbstractContainer) obj;

                        aContainer.add(basicItem);
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    for (AbstractEntity obj : GameManager.getFullInventory()) {
                        if (obj instanceof AbstractContainer && containerId.equals(obj.getId())) {
                            AbstractContainer aContainer = (AbstractContainer) obj;

                            aContainer.add(basicItem);
                            break;
                        }
                    }
                }
            }
        }

        stm.close();

        stm = connection.prepareStatement("SELECT * FROM SAVEDATA.BasicObject");
        resultSet = stm.executeQuery();

        while (resultSet.next()) {
            found = false;
            String roomId = resultSet.getString(4);
            String containerId = resultSet.getString(5);

            BasicObject obj = new BasicObject(resultSet);

            if (!roomId.equals("null")) {
                for (AbstractRoom room : RoomsLoader.listAllRooms(rooms)) {
                    if (roomId.equals(room.getId())) {
                        PlayableRoom pRoom = (PlayableRoom) room;
                        pRoom.getObjects().add(obj);
                        break;
                    }
                }
            } else if (!containerId.equals("null")) {
                for (AbstractEntity entity : RoomsLoader.mapAllObjects(rooms).values()) {
                    if (entity instanceof AbstractContainer && containerId.equals(entity.getId())) {
                        AbstractContainer aContainer = (AbstractContainer) entity;

                        aContainer.add(obj);
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    for (AbstractEntity _obj : GameManager.getFullInventory()) {
                        if (_obj instanceof AbstractContainer && containerId.equals(_obj.getId())) {
                            AbstractContainer aContainer = (AbstractContainer) _obj;

                            aContainer.add(obj);
                            break;
                        }
                    }
                }
            }
        }

        stm.close();

        stm = connection.prepareStatement("SELECT * FROM SAVEDATA.Door");
        resultSet = stm.executeQuery();

        while (resultSet.next()) {
            found = false;
            String roomId = resultSet.getString(4);

            Door obj = new Door(resultSet);

            if (!roomId.equals("null")) {
                for (AbstractRoom room : RoomsLoader.listAllRooms(rooms)) {
                    if (roomId.equals(room.getId())) {
                        PlayableRoom pRoom = (PlayableRoom) room;
                        pRoom.getObjects().add(obj);
                        break;
                    }
                }
            }
        }

        stm.close();

        stm = connection.prepareStatement("SELECT * FROM SAVEDATA.FillableItem");
        resultSet = stm.executeQuery();

        while (resultSet.next()) {
            String roomId = resultSet.getString(4);
            String containerId = resultSet.getString(5);

            FillableItem obj = new FillableItem(resultSet);

            if (obj.isPickedUp()) {
                GameManager.getInventory().add(obj);
            } else if (!roomId.equals("null")) {
                for (AbstractRoom room : RoomsLoader.listAllRooms(rooms)) {
                    if (roomId.equals(room.getId())) {
                        PlayableRoom pRoom = (PlayableRoom) room;
                        pRoom.getObjects().add(obj);
                        break;
                    }
                }
            } else if (!containerId.equals("null")) {
                for (AbstractEntity entity : RoomsLoader.mapAllObjects(rooms).values()) {
                    if (entity instanceof AbstractContainer && containerId.equals(entity.getId())) {
                        AbstractContainer aContainer = (AbstractContainer) entity;

                        aContainer.add(obj);
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    for (AbstractEntity _obj : GameManager.getFullInventory()) {
                        if (_obj instanceof AbstractContainer && containerId.equals(_obj.getId())) {
                            AbstractContainer aContainer = (AbstractContainer) _obj;

                            aContainer.add(obj);
                            break;
                        }
                    }
                }
            }
        }

        stm.close();

        stm = connection.prepareStatement("SELECT * FROM SAVEDATA.FireObject");
        resultSet = stm.executeQuery();

        while (resultSet.next()) {
            found = false;
            String roomId = resultSet.getString(4);
            String containerId = resultSet.getString(5);

            FireObject obj = new FireObject(resultSet);

            if (!roomId.equals("null")) {
                for (AbstractRoom room : RoomsLoader.listAllRooms(rooms)) {
                    if (roomId.equals(room.getId())) {
                        PlayableRoom pRoom = (PlayableRoom) room;
                        pRoom.getObjects().add(obj);
                        break;
                    }
                }
            } else if (!containerId.equals("null")) {
                for (AbstractEntity entity : RoomsLoader.mapAllObjects(rooms).values()) {
                    if (entity instanceof AbstractContainer && containerId.equals(entity.getId())) {
                        AbstractContainer aContainer = (AbstractContainer) entity;

                        aContainer.add(obj);
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    for (AbstractEntity _obj : GameManager.getFullInventory()) {
                        if (_obj instanceof AbstractContainer && containerId.equals(_obj.getId())) {
                            AbstractContainer aContainer = (AbstractContainer) _obj;

                            aContainer.add(obj);
                            break;
                        }
                    }
                }
            }
        }

        stm.close();

        stm = connection.prepareStatement("SELECT * FROM SAVEDATA.FluidItem");
        resultSet = stm.executeQuery();

        while (resultSet.next()) {
            found = false;
            String roomId = resultSet.getString(4);
            String containerId = resultSet.getString(5);

            FluidItem obj = new FluidItem(resultSet);

            if (obj.isPickedUp()) {
                GameManager.getInventory().add(obj);
            } else if (!roomId.equals("null")) {
                for (AbstractRoom room : RoomsLoader.listAllRooms(rooms)) {
                    if (roomId.equals(room.getId())) {
                        PlayableRoom pRoom = (PlayableRoom) room;
                        pRoom.getObjects().add(obj);
                        break;
                    }
                }
            } else if (!containerId.equals("null")) {
                for (AbstractEntity entity : RoomsLoader.mapAllObjects(rooms).values()) {
                    if (entity instanceof AbstractContainer && containerId.equals(entity.getId())) {
                        AbstractContainer aContainer = (AbstractContainer) entity;

                        aContainer.add(obj);
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    for (AbstractEntity _obj : GameManager.getFullInventory()) {
                        if (_obj instanceof AbstractContainer && containerId.equals(_obj.getId())) {
                            AbstractContainer aContainer = (AbstractContainer) _obj;

                            aContainer.add(obj);
                            break;
                        }
                    }
                }
            }
        }

        stm.close();

        stm = connection.prepareStatement("SELECT * FROM SAVEDATA.Human");
        resultSet = stm.executeQuery();

        while (resultSet.next()) {
            found = false;
            String roomId = resultSet.getString(4);
            String containerId = resultSet.getString(5);

            Human obj = new Human(resultSet);

            PreparedStatement phrsStm =
                    connection.prepareStatement("SELECT * FROM SAVEDATA.HumanPhrases");
            ResultSet rs = phrsStm.executeQuery();

            while (rs.next()) {
                String _id = resultSet.getString(1);
                String _text = resultSet.getString(2);

                if (_id.equals(obj.getId())) {
                    obj.queuePhrase(_text);
                }
            }

            phrsStm.close();

            if (!roomId.equals("null")) {
                for (AbstractRoom room : RoomsLoader.listAllRooms(rooms)) {
                    if (roomId.equals(room.getId())) {
                        PlayableRoom pRoom = (PlayableRoom) room;
                        pRoom.getObjects().add(obj);
                        break;
                    }
                }
            } else if (!containerId.equals("null")) {
                for (AbstractEntity entity : RoomsLoader.mapAllObjects(rooms).values()) {
                    if (entity instanceof AbstractContainer && containerId.equals(entity.getId())) {
                        AbstractContainer aContainer = (AbstractContainer) entity;

                        aContainer.add(obj);
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    for (AbstractEntity _obj : GameManager.getFullInventory()) {
                        if (_obj instanceof AbstractContainer && containerId.equals(_obj.getId())) {
                            AbstractContainer aContainer = (AbstractContainer) _obj;

                            aContainer.add(obj);
                            break;
                        }
                    }
                }
            }
        }

        stm.close();

        stm = connection.prepareStatement("SELECT * FROM SAVEDATA.InvisibleWall");
        resultSet = stm.executeQuery();

        while (resultSet.next()) {
            found = false;
            String roomId = resultSet.getString(4);

            InvisibleWall obj = new InvisibleWall(resultSet);

            if (!roomId.equals("null")) {
                for (AbstractRoom room : RoomsLoader.listAllRooms(rooms)) {
                    if (roomId.equals(room.getId())) {
                        PlayableRoom pRoom = (PlayableRoom) room;
                        pRoom.getObjects().add(obj);
                        break;
                    }
                }
            }
        }

        stm.close();

        stm = connection.prepareStatement("SELECT * FROM SAVEDATA.LightSourceItem");
        resultSet = stm.executeQuery();

        while (resultSet.next()) {
            String roomId = resultSet.getString(4);
            String containerId = resultSet.getString(5);

            LightSourceItem obj = new LightSourceItem(resultSet);

            if (obj.isPickedUp()) {
                GameManager.getInventory().add(obj);
            } else if (!roomId.equals("null")) {
                for (AbstractRoom room : RoomsLoader.listAllRooms(rooms)) {
                    if (roomId.equals(room.getId())) {
                        PlayableRoom pRoom = (PlayableRoom) room;
                        pRoom.getObjects().add(obj);
                        break;
                    }
                }
            } else if (!containerId.equals("null")) {
                for (AbstractEntity entity : RoomsLoader.mapAllObjects(rooms).values()) {
                    if (entity instanceof AbstractContainer && containerId.equals(entity.getId())) {
                        AbstractContainer aContainer = (AbstractContainer) entity;

                        aContainer.add(obj);
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    for (AbstractEntity _obj : GameManager.getFullInventory()) {
                        if (_obj instanceof AbstractContainer && containerId.equals(_obj.getId())) {
                            AbstractContainer aContainer = (AbstractContainer) _obj;

                            aContainer.add(obj);
                            break;
                        }
                    }
                }
            }
        }

        stm.close();

        stm = connection.prepareStatement("SELECT * FROM SAVEDATA.MovableObject");
        resultSet = stm.executeQuery();

        while (resultSet.next()) {
            found = false;
            String roomId = resultSet.getString(4);
            String containerId = resultSet.getString(5);

            MovableObject obj = new MovableObject(resultSet);

            if (!roomId.equals("null")) {
                for (AbstractRoom room : RoomsLoader.listAllRooms(rooms)) {
                    if (roomId.equals(room.getId())) {
                        PlayableRoom pRoom = (PlayableRoom) room;
                        pRoom.getObjects().add(obj);
                        break;
                    }
                }
            } else if (!containerId.equals("null")) {
                for (AbstractEntity entity : RoomsLoader.mapAllObjects(rooms).values()) {
                    if (entity instanceof AbstractContainer && containerId.equals(entity.getId())) {
                        AbstractContainer aContainer = (AbstractContainer) entity;

                        aContainer.add(obj);
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    for (AbstractEntity _obj : GameManager.getFullInventory()) {
                        if (_obj instanceof AbstractContainer && containerId.equals(_obj.getId())) {
                            AbstractContainer aContainer = (AbstractContainer) _obj;

                            aContainer.add(obj);
                            break;
                        }
                    }
                }
            }
        }

        stm.close();

        stm = connection.prepareStatement("SELECT * FROM SAVEDATA.PullableObject");
        resultSet = stm.executeQuery();

        while (resultSet.next()) {
            found = false;
            String roomId = resultSet.getString(4);
            String containerId = resultSet.getString(5);

            PullableObject obj = new PullableObject(resultSet);

            if (!roomId.equals("null")) {
                for (AbstractRoom room : RoomsLoader.listAllRooms(rooms)) {
                    if (roomId.equals(room.getId())) {
                        PlayableRoom pRoom = (PlayableRoom) room;
                        pRoom.getObjects().add(obj);
                        break;
                    }
                }
            } else if (!containerId.equals("null")) {
                for (AbstractEntity entity : RoomsLoader.mapAllObjects(rooms).values()) {
                    if (entity instanceof AbstractContainer && containerId.equals(entity.getId())) {
                        AbstractContainer aContainer = (AbstractContainer) entity;

                        aContainer.add(obj);
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    for (AbstractEntity _obj : GameManager.getFullInventory()) {
                        if (_obj instanceof AbstractContainer && containerId.equals(_obj.getId())) {
                            AbstractContainer aContainer = (AbstractContainer) _obj;

                            aContainer.add(obj);
                            break;
                        }
                    }
                }
            }
        }

        stm.close();

        stm = connection.prepareStatement("SELECT * FROM SAVEDATA.PushableObject");
        resultSet = stm.executeQuery();

        while (resultSet.next()) {
            found = false;
            String roomId = resultSet.getString(4);
            String containerId = resultSet.getString(5);

            PushableObject obj = new PushableObject(resultSet);

            if (!roomId.equals("null")) {
                for (AbstractRoom room : RoomsLoader.listAllRooms(rooms)) {
                    if (roomId.equals(room.getId())) {
                        PlayableRoom pRoom = (PlayableRoom) room;
                        pRoom.getObjects().add(obj);
                        break;
                    }
                }
            } else if (!containerId.equals("null")) {
                for (AbstractEntity entity : RoomsLoader.mapAllObjects(rooms).values()) {
                    if (entity instanceof AbstractContainer && containerId.equals(entity.getId())) {
                        AbstractContainer aContainer = (AbstractContainer) entity;

                        aContainer.add(obj);
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    for (AbstractEntity _obj : GameManager.getFullInventory()) {
                        if (_obj instanceof AbstractContainer && containerId.equals(_obj.getId())) {
                            AbstractContainer aContainer = (AbstractContainer) _obj;

                            aContainer.add(obj);
                            break;
                        }
                    }
                }
            }
        }

        stm.close();

        stm = connection.prepareStatement("SELECT * FROM SAVEDATA.ReadableItem");
        resultSet = stm.executeQuery();

        while (resultSet.next()) {
            found = false;
            String roomId = resultSet.getString(4);
            String containerId = resultSet.getString(5);

            ReadableItem obj = new ReadableItem(resultSet);

            if (obj.isPickedUp()) {
                GameManager.getInventory().add(obj);
            } else if (!roomId.equals("null")) {
                for (AbstractRoom room : RoomsLoader.listAllRooms(rooms)) {
                    if (roomId.equals(room.getId())) {
                        PlayableRoom pRoom = (PlayableRoom) room;
                        pRoom.getObjects().add(obj);
                        break;
                    }
                }
            } else if (!containerId.equals("null")) {
                for (AbstractEntity entity : RoomsLoader.mapAllObjects(rooms).values()) {
                    if (entity instanceof AbstractContainer && containerId.equals(entity.getId())) {
                        AbstractContainer aContainer = (AbstractContainer) entity;

                        aContainer.add(obj);
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    for (AbstractEntity _obj : GameManager.getFullInventory()) {
                        if (_obj instanceof AbstractContainer && containerId.equals(_obj.getId())) {
                            AbstractContainer aContainer = (AbstractContainer) _obj;

                            aContainer.add(obj);
                            break;
                        }
                    }
                }
            }
        }

        stm.close();

        stm = connection.prepareStatement("SELECT * FROM SAVEDATA.UnopenableDoor");
        resultSet = stm.executeQuery();

        while (resultSet.next()) {
            found = false;
            String roomId = resultSet.getString(4);
            String containerId = resultSet.getString(5);

            UnopenableDoor obj = new UnopenableDoor(resultSet);

            if (!roomId.equals("null")) {
                for (AbstractRoom room : RoomsLoader.listAllRooms(rooms)) {
                    if (roomId.equals(room.getId())) {
                        PlayableRoom pRoom = (PlayableRoom) room;
                        pRoom.getObjects().add(obj);
                        break;
                    }
                }
            } else if (!containerId.equals("null")) {
                for (AbstractEntity entity : RoomsLoader.mapAllObjects(rooms).values()) {
                    if (entity instanceof AbstractContainer && containerId.equals(entity.getId())) {
                        AbstractContainer aContainer = (AbstractContainer) entity;

                        aContainer.add(obj);
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    for (AbstractEntity _obj : GameManager.getFullInventory()) {
                        if (_obj instanceof AbstractContainer && containerId.equals(_obj.getId())) {
                            AbstractContainer aContainer = (AbstractContainer) _obj;

                            aContainer.add(obj);
                            break;
                        }
                    }
                }
            }
        }

        stm.close();

        stm = connection.prepareStatement("SELECT * FROM SAVEDATA.WearableItem");
        resultSet = stm.executeQuery();

        while (resultSet.next()) {
            found = false;
            String roomId = resultSet.getString(4);
            String containerId = resultSet.getString(5);

            WearableItem obj = new WearableItem(resultSet);

            if (obj.isPickedUp()) {
                GameManager.getInventory().add(obj);
            } else if (!roomId.equals("null")) {
                for (AbstractRoom room : RoomsLoader.listAllRooms(rooms)) {
                    if (roomId.equals(room.getId())) {
                        PlayableRoom pRoom = (PlayableRoom) room;
                        pRoom.getObjects().add(obj);
                        break;
                    }
                }
            } else if (!containerId.equals("null")) {
                for (AbstractEntity entity : RoomsLoader.mapAllObjects(rooms).values()) {
                    if (entity instanceof AbstractContainer && containerId.equals(entity.getId())) {
                        AbstractContainer aContainer = (AbstractContainer) entity;

                        aContainer.add(obj);
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    for (AbstractEntity _obj : GameManager.getFullInventory()) {
                        if (_obj instanceof AbstractContainer && containerId.equals(_obj.getId())) {
                            AbstractContainer aContainer = (AbstractContainer) _obj;

                            aContainer.add(obj);
                            break;
                        }
                    }
                }
            }
        }

        stm.close();
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
            rooms.add(room);
            evtStm.close();
            evtResultSet.close();
        }
        stm.close();
        resultSet.close();

        stm = connection.prepareStatement("SELECT * FROM SAVEDATA.CutsceneRoom");
        resultSet = stm.executeQuery();

        while (resultSet.next()) {
            CutsceneRoom room = new CutsceneRoom(resultSet);

            rooms.add(room);
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