package di.uniba.map.b.adventure.entities.pickupable;

import java.util.Set;
import di.uniba.map.b.adventure.entities.interfaces.IReadable;

public class ReadableItem extends BasicItem implements IReadable {

    private String readText;

    public ReadableItem(int id, String name, String description) {
        super(id, name, description);
    }

    public ReadableItem(int id, String name, String description, Set<String> alias) {
        super(id, name, description, alias);
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
