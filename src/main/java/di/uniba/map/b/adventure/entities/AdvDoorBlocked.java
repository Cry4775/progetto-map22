package di.uniba.map.b.adventure.entities;

import java.util.Set;

public class AdvDoorBlocked extends AdvObject {

    private String openEventText;

    public AdvDoorBlocked(int id, String name, String description, Set<String> alias) {
        super(id, name, description, alias);
    }

    public AdvDoorBlocked(int id) {
        super(id);
    }

    public AdvDoorBlocked(int id, String name) {
        super(id, name);
    }

    public AdvDoorBlocked(int id, String name, String description) {
        super(id, name, description);
    }

    public String getOpenEventText() {
        return openEventText;
    }

    public void setOpenEventText(String openEventText) {
        this.openEventText = openEventText;
    }

}
