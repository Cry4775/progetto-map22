package di.uniba.map.b.adventure.type;

import java.util.Set;

public class AdvPerson extends AdvObject {

    public AdvPerson(int id) {
        super(id);
    }

    public AdvPerson(int id, String name) {
        super(id, name);
    }

    public AdvPerson(int id, String name, String description) {
        super(id, name, description);
    }

    public AdvPerson(int id, String name, String description, Set<String> alias) {
        super(id, name, description, alias);
    }

}
