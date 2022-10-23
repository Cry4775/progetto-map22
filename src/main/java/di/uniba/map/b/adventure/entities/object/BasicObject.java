package di.uniba.map.b.adventure.entities.object;

import java.util.List;
import java.util.Set;
import com.google.common.collect.Multimap;
import di.uniba.map.b.adventure.entities.AbstractEntity;
import di.uniba.map.b.adventure.type.AbstractRoom;

public class BasicObject extends AbstractEntity {

    public BasicObject(int id, String name, String description) {
        super(id, name, description);
    }

    public BasicObject(int id, String name, String description, Set<String> alias) {
        super(id, name, description, alias);
    }

    @Override
    public void processReferences(Multimap<Integer, AbstractEntity> objects,
            List<AbstractRoom> rooms) {
        processRoomParent(rooms);
        processEventReferences(objects, rooms);
    }

}
