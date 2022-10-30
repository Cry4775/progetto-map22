package component.entity.humanoid;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import com.google.common.collect.Multimap;
import component.entity.AbstractEntity;
import component.entity.interfaces.ITalkable;
import component.event.EventType;
import component.room.AbstractRoom;

public class Human extends AbstractEntity implements ITalkable {

    private Queue<String> phrases = new LinkedList<>();

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
}
