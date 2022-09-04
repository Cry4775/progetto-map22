package di.uniba.map.b.adventure.entities;

import java.util.List;
import java.util.Set;
import di.uniba.map.b.adventure.type.Room;

public class AdvFixedObject extends AbstractEntity {

    public AdvFixedObject(int id, String name, String description, Set<String> alias) {
        super(id, name, description, alias);
    }

    public AdvFixedObject(int id) {
        super(id);
    }

    public AdvFixedObject(int id, String name) {
        super(id, name);
    }

    public AdvFixedObject(int id, String name, String description) {
        super(id, name, description);
    }

    @Override
    public void processReferences(List<AbstractEntity> objects, List<Room> rooms) {
        processEventReferences(objects, rooms);
    }

}
