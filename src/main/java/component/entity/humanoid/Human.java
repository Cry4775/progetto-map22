package component.entity.humanoid;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import component.entity.AbstractEntity;
import component.entity.interfaces.ITalkable;
import component.event.EventType;
import component.room.AbstractRoom;
import engine.Inventory;
import engine.database.DBManager;
import gui.GUIManager;

public class Human extends AbstractEntity implements ITalkable {

    private Queue<String> phrases = new LinkedList<>();

    public Human(ResultSet resultSet) throws SQLException {
        super(resultSet);

        PreparedStatement phrsStm =
                DBManager.getConnection().prepareStatement(
                        "SELECT * FROM SAVEDATA.HumanPhrases WHERE ID = " + getId());
        ResultSet rs = phrsStm.executeQuery();

        while (rs.next()) {
            String text = rs.getString(2);

            queuePhrase(text);
        }

        phrsStm.close();
    }

    public void queuePhrase(String phrase) {
        phrases.add(phrase);
    }

    @Override
    public boolean talk() {
        if (!canInteract())
            return false;

        if (phrases != null) {
            if (!phrases.isEmpty()) {
                if (phrases.size() > 1) {
                    GUIManager.appendOutput(phrases.poll());
                } else {
                    GUIManager.appendOutput(phrases.peek());
                }
            } else {
                GUIManager.appendOutput("...");
            }
        } else {
            GUIManager.appendOutput("...");
        }

        triggerEvent(EventType.TALK_WITH);
        return true;
    }

    @Override
    public void saveOnDB() throws SQLException {
        PreparedStatement stm = DBManager.getConnection().prepareStatement(
                "INSERT INTO SAVEDATA.Human values (?, ?, ?, ?, ?)");

        super.setKnownValuesOnStatement(stm);
        stm.executeUpdate();

        if (phrases != null) {

            stm = DBManager.getConnection()
                    .prepareStatement("INSERT INTO SAVEDATA.HumanPhrases values (?, ?)");
            for (String string : phrases) {
                stm.setString(1, getId());
                stm.setString(2, string);
                stm.executeUpdate();
            }
        }

        saveExternalsOnDB();
    }

    public static void loadFromDB(List<AbstractRoom> allRooms, Inventory inventory) throws SQLException {
        PreparedStatement stm =
                DBManager.getConnection()
                        .prepareStatement("SELECT * FROM SAVEDATA.Human");
        ResultSet resultSet = stm.executeQuery();

        while (resultSet.next()) {
            Human obj = new Human(resultSet);

            obj.loadLocation(resultSet, allRooms, inventory);
            obj.loadObjEvents();
        }

        stm.close();
    }

}
