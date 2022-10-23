package di.uniba.map.b.adventure.entities.doorlike;

import java.util.List;
import java.util.Set;
import com.google.common.collect.Multimap;
import di.uniba.map.b.adventure.entities.AbstractEntity;
import di.uniba.map.b.adventure.type.AbstractRoom;

public class UnopenableDoor extends AbstractEntity {

    private String openEventText; // TODO porting a evento

    public UnopenableDoor(int id, String name, String description) {
        super(id, name, description);
    }

    public UnopenableDoor(int id, String name, String description, Set<String> alias) {
        super(id, name, description, alias);
    }

    public String getOpenEventText() {
        return openEventText;
    }

    public void setOpenEventText(String openEventText) {
        this.openEventText = openEventText;
    }

    @Override
    public void processReferences(Multimap<Integer, AbstractEntity> objects,
            List<AbstractRoom> rooms) {
        processRoomParent(rooms);
        processEventReferences(objects, rooms);
    }

}
