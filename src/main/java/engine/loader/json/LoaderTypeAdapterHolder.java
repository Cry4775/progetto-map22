package engine.loader.json;

import component.entity.AbstractEntity;
import component.entity.container.BasicContainer;
import component.entity.container.ChestlikeContainer;
import component.entity.container.SocketlikeContainer;
import component.entity.container.pickupable.WearableContainer;
import component.entity.doorlike.Door;
import component.entity.doorlike.InvisibleWall;
import component.entity.doorlike.UnopenableDoor;
import component.entity.humanoid.Human;
import component.entity.object.BasicObject;
import component.entity.object.FireObject;
import component.entity.object.MovableObject;
import component.entity.object.PullableObject;
import component.entity.object.PushableObject;
import component.entity.pickupable.BasicItem;
import component.entity.pickupable.FillableItem;
import component.entity.pickupable.FluidItem;
import component.entity.pickupable.LightSourceItem;
import component.entity.pickupable.ReadableItem;
import component.entity.pickupable.WearableItem;
import component.event.AbstractEvent;
import component.event.ObjectEvent;
import component.event.RoomEvent;
import component.room.AbstractRoom;
import component.room.CutsceneRoom;
import component.room.MutableRoom;
import component.room.PlayableRoom;

/**
 * Holds all the {@link RuntimeTypeAdapterFactory} that are needed for loading a JSON for the game.
 */
public class LoaderTypeAdapterHolder {
    private static final RuntimeTypeAdapterFactory<AbstractEntity> typeAdapterEntities;
    private static final RuntimeTypeAdapterFactory<AbstractRoom> typeAdapterRooms;
    private static final RuntimeTypeAdapterFactory<AbstractEvent> typeAdapterEvents;

    static {
        typeAdapterEntities = RuntimeTypeAdapterFactory
                .of(AbstractEntity.class)
                .registerSubtype(BasicObject.class)
                .registerSubtype(BasicItem.class)
                .registerSubtype(BasicContainer.class)
                .registerSubtype(WearableContainer.class)
                .registerSubtype(ChestlikeContainer.class)
                .registerSubtype(SocketlikeContainer.class)
                .registerSubtype(UnopenableDoor.class)
                .registerSubtype(Door.class)
                .registerSubtype(InvisibleWall.class)
                .registerSubtype(WearableItem.class)
                .registerSubtype(FillableItem.class)
                .registerSubtype(ReadableItem.class)
                .registerSubtype(LightSourceItem.class)
                .registerSubtype(MovableObject.class)
                .registerSubtype(PullableObject.class)
                .registerSubtype(PushableObject.class)
                .registerSubtype(FireObject.class)
                .registerSubtype(FluidItem.class)
                .registerSubtype(Human.class);

        typeAdapterRooms = RuntimeTypeAdapterFactory
                .of(AbstractRoom.class)
                .registerSubtype(PlayableRoom.class)
                .registerSubtype(MutableRoom.class)
                .registerSubtype(CutsceneRoom.class);

        typeAdapterEvents = RuntimeTypeAdapterFactory
                .of(AbstractEvent.class)
                .registerSubtype(ObjectEvent.class)
                .registerSubtype(RoomEvent.class);
    }

    public enum AdapterType {
        ENTITIES,
        ROOMS,
        EVENTS
    }

    public static RuntimeTypeAdapterFactory<?> get(AdapterType type) {
        switch (type) {
            case ENTITIES:
                return typeAdapterEntities;
            case ROOMS:
                return typeAdapterRooms;
            case EVENTS:
                return typeAdapterEvents;
            default:
                return null;
        }
    }

}
