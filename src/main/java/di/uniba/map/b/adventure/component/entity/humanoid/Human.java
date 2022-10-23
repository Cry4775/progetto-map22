package di.uniba.map.b.adventure.component.entity.humanoid;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import com.google.common.collect.Multimap;
import di.uniba.map.b.adventure.type.EventType;
import di.uniba.map.b.adventure.component.entity.AbstractEntity;
import di.uniba.map.b.adventure.component.entity.interfaces.ITalkable;
import di.uniba.map.b.adventure.component.room.AbstractRoom;

public class Human extends AbstractEntity implements ITalkable {

    private Queue<String> phrases = new LinkedList<>();

    public Human(int id, String name, String description) {
        super(id, name, description);
    }

    public Human(int id, String name, String description, Set<String> alias) {
        super(id, name, description, alias);
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
    public void processReferences(Multimap<Integer, AbstractEntity> objects,
            List<AbstractRoom> rooms) {
        processRoomParent(rooms);
        processEventReferences(objects, rooms);
    }
}
