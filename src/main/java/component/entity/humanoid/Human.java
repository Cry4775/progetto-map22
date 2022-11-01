package component.entity.humanoid;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import com.google.common.collect.Multimap;
import component.entity.AbstractEntity;
import component.entity.container.AbstractContainer;
import component.entity.interfaces.ITalkable;
import component.event.EventType;
import component.room.AbstractRoom;
import component.room.PlayableRoom;

public class Human extends AbstractEntity implements ITalkable {

    public Human(ResultSet resultSet) throws SQLException {
        super(resultSet);
    }

    private Queue<String> phrases = new LinkedList<>();

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

        outString.append(processEvent(EventType.TALK_WITH));
        setActionPerformed(true);
        return outString;
    }

    @Override
    public void processReferences(Multimap<String, AbstractEntity> objects,
            List<AbstractRoom> rooms) {
        processRoomParent(rooms);
        processEventReferences(objects, rooms);
    }

    @Override
    public void saveOnDB(Connection connection) throws SQLException {
        PreparedStatement stm = connection.prepareStatement(
                "INSERT INTO SAVEDATA.Human values (?, ?, ?, ?, ?)");

        stm.setString(1, getId());
        stm.setString(2, getName());
        stm.setString(3, getDescription());

        if (getParent() instanceof PlayableRoom) {
            stm.setString(4, getClosestRoomParent().getId());
            stm.setString(5, "null");
        } else if (getParent() instanceof AbstractContainer) {
            stm.setString(4, "null");
            stm.setString(5, getParent().getId());
        }

        stm.executeUpdate();

        if (phrases != null) {

            stm = connection.prepareStatement("INSERT INTO SAVEDATA.HumanPhrases values (?, ?)");
            for (String string : phrases) {
                stm.setString(1, getId());
                stm.setString(2, string);
                stm.executeUpdate();
            }
        }

        saveExternalsOnDB(connection);
    }

}
