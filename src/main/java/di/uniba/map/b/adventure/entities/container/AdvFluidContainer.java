package di.uniba.map.b.adventure.entities.container;

import java.util.Set;

public class AdvFluidContainer extends AbstractContainer {

    public AdvFluidContainer(int id, String name, String description, Set<String> alias) {
        super(id, name, description, alias);
    }

    public AdvFluidContainer(int id) {
        super(id);
    }

    public AdvFluidContainer(int id, String name) {
        super(id, name);
    }

    public AdvFluidContainer(int id, String name, String description) {
        super(id, name, description);
    }

}
