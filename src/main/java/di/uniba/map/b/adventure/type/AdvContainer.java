package di.uniba.map.b.adventure.type;

import java.util.Set;

public class AdvContainer extends AbstractContainer {

    public AdvContainer(int id, String name, String description, Set<String> alias) {
        super(id, name, description, alias);
    }

    public AdvContainer(int id) {
        super(id);
    }

    public AdvContainer(int id, String name) {
        super(id, name);
    }

    public AdvContainer(int id, String name, String description) {
        super(id, name, description);
    }

}
