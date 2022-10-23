package di.uniba.map.b.adventure.component.entity.container;

import java.util.Set;

public class BasicContainer extends AbstractContainer {

    public BasicContainer(int id, String name, String description) {
        super(id, name, description);
    }

    public BasicContainer(int id, String name, String description, Set<String> alias) {
        super(id, name, description, alias);
    }

}
