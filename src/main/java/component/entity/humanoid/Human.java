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
import engine.database.DBManager;

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
    public StringBuilder talk() {
        StringBuilder outString = new StringBuilder();

        if (phrases != null) {
            if (!phrases.isEmpty()) {
                if (phrases.size() > 1) {
                    outString.append(phrases.poll());
                } else {
                    outString.append(phrases.peek());
                }
            } else {
                outString.append("...");
            }
        } else {
            outString.append("...");
        }

        outString.append(triggerEvent(EventType.TALK_WITH));
        setActionPerformed(true);
        return outString;
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

    public static void loadFromDB(List<AbstractRoom> allRooms) throws SQLException {
        PreparedStatement stm =
                DBManager.getConnection()
                        .prepareStatement("SELECT * FROM SAVEDATA.Human");
        ResultSet resultSet = stm.executeQuery();

        while (resultSet.next()) {
            Human obj = new Human(resultSet);

            obj.loadLocation(resultSet, allRooms);
            obj.loadObjEvents();
        }

        stm.close();
    }

}
