package di.uniba.map.b.adventure.engine.loader.json;

import di.uniba.map.b.adventure.component.entity.AbstractEntity;
import di.uniba.map.b.adventure.component.entity.container.BasicContainer;
import di.uniba.map.b.adventure.component.entity.container.ChestlikeContainer;
import di.uniba.map.b.adventure.component.entity.container.SocketlikeContainer;
import di.uniba.map.b.adventure.component.entity.container.pickupable.WearableContainer;
import di.uniba.map.b.adventure.component.entity.doorlike.Door;
import di.uniba.map.b.adventure.component.entity.doorlike.InvisibleWall;
import di.uniba.map.b.adventure.component.entity.doorlike.UnopenableDoor;
import di.uniba.map.b.adventure.component.entity.humanoid.Human;
import di.uniba.map.b.adventure.component.entity.object.BasicObject;
import di.uniba.map.b.adventure.component.entity.object.FireObject;
import di.uniba.map.b.adventure.component.entity.object.MovableObject;
import di.uniba.map.b.adventure.component.entity.object.PullableObject;
import di.uniba.map.b.adventure.component.entity.object.PushableObject;
import di.uniba.map.b.adventure.component.entity.pickupable.BasicItem;
import di.uniba.map.b.adventure.component.entity.pickupable.FillableItem;
import di.uniba.map.b.adventure.component.entity.pickupable.FluidItem;
import di.uniba.map.b.adventure.component.entity.pickupable.LightSourceItem;
import di.uniba.map.b.adventure.component.entity.pickupable.ReadableItem;
import di.uniba.map.b.adventure.component.entity.pickupable.WearableItem;
import di.uniba.map.b.adventure.component.event.AbstractEvent;
import di.uniba.map.b.adventure.component.event.ObjectEvent;
import di.uniba.map.b.adventure.component.event.RoomEvent;
import di.uniba.map.b.adventure.component.room.AbstractRoom;
import di.uniba.map.b.adventure.component.room.CutsceneRoom;
import di.uniba.map.b.adventure.component.room.MutableRoom;
import di.uniba.map.b.adventure.component.room.PlayableRoom;

public class TypeAdapterHolder {
    private static final RuntimeTypeAdapterFactory<AbstractEntity> typeAdapterEntities;
    private static final RuntimeTypeAdapterFactory<AbstractRoom> typeAdapterRooms;
    private static final RuntimeTypeAdapterFactory<AbstractEvent> typeAdapterEvents;

    static {
        typeAdapterEntities =
                RuntimeTypeAdapterFactory
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

        typeAdapterEvents =
                RuntimeTypeAdapterFactory
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
