package di.uniba.map.b.adventure.entities;

import java.util.Set;

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

}
