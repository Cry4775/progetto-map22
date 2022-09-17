package di.uniba.map.b.adventure.entities;

import java.util.List;
import java.util.Set;
import di.uniba.map.b.adventure.type.AbstractRoom;

public class AdvFixedObject extends AbstractEntity {

    public AdvFixedObject(int id, String name, String description) {
        super(id, name, description);
    }

    public AdvFixedObject(int id, String name, String description, Set<String> alias) {
        super(id, name, description, alias);
    }

    @Override
    public void processReferences(List<AbstractEntity> objects, List<AbstractRoom> rooms) {
        processRoomParent(rooms);
        processEventReferences(objects, rooms);
    }

}
