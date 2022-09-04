package di.uniba.map.b.adventure.entities;

import java.util.List;
import java.util.Set;
import di.uniba.map.b.adventure.type.Room;

public class AdvDoorBlocked extends AbstractEntity {

    private String openEventText; // TODO porting a evento

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

    @Override
    public void processReferences(List<AbstractEntity> objects, List<Room> rooms) {
        processEventReferences(objects, rooms);
    }

}
