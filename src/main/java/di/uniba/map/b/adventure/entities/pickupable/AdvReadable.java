package di.uniba.map.b.adventure.entities.pickupable;

import java.util.Set;
import di.uniba.map.b.adventure.entities.IReadable;

public class AdvReadable extends AdvItem implements IReadable {

    private String readText;

    public AdvReadable(int id, String name, String description, Set<String> alias) {
        super(id, name, description, alias);
    }

    public AdvReadable(int id) {
        super(id);
    }

    public AdvReadable(int id, String name) {
        super(id, name);
    }

    public AdvReadable(int id, String name, String description) {
        super(id, name, description);
    }

    @Override
    public StringBuilder read() {
        StringBuilder outString = new StringBuilder();

        if (readText != null && !readText.isEmpty()) {
            outString.append(readText);
        } else {
            outString.append("Non c'é scritto nulla.");
        }

        return outString;
    }

}
