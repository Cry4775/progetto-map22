package component.entity.doorlike;

import java.util.List;
import com.google.common.collect.Multimap;
import component.entity.AbstractEntity;
import component.room.AbstractRoom;

public class UnopenableDoor extends AbstractEntity {

    private String openEventText; // TODO porting a evento

    public String getOpenEventText() {
        return openEventText;
    }

    public void setOpenEventText(String openEventText) {
        this.openEventText = openEventText;
    }

    @Override
    public void processReferences(Multimap<String, AbstractEntity> objects,
            List<AbstractRoom> rooms) {
        processRoomParent(rooms);
        processEventReferences(objects, rooms);
    }

}
