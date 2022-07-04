package di.uniba.map.b.adventure.type;

import java.util.Set;

public class AdvFakeDoor extends AdvObject {

    private String openEventText;

    public AdvFakeDoor(int id, String name, String description, Set<String> alias) {
        super(id, name, description, alias);
    }

    public AdvFakeDoor(int id) {
        super(id);
    }

    public AdvFakeDoor(int id, String name) {
        super(id, name);
    }

    public AdvFakeDoor(int id, String name, String description) {
        super(id, name, description);
    }

    public String getOpenEventText() {
        return openEventText;
    }

    public void setOpenEventText(String openEventText) {
        this.openEventText = openEventText;
    }

}
