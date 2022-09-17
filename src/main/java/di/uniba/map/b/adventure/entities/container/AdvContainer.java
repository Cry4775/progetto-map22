package di.uniba.map.b.adventure.entities.container;

import java.util.Set;

public class AdvContainer extends AbstractContainer {

    public AdvContainer(int id, String name, String description) {
        super(id, name, description);
    }

    public AdvContainer(int id, String name, String description, Set<String> alias) {
        super(id, name, description, alias);
    }

}
