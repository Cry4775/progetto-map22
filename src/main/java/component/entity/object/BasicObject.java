package component.entity.object;

import java.util.List;
import com.google.common.collect.Multimap;
import component.entity.AbstractEntity;
import component.room.AbstractRoom;

public class BasicObject extends AbstractEntity {

    @Override
    public void processReferences(Multimap<String, AbstractEntity> objects,
            List<AbstractRoom> rooms) {
        processRoomParent(rooms);
        processEventReferences(objects, rooms);
    }

}
