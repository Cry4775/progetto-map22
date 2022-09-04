package di.uniba.map.b.adventure.entities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import di.uniba.map.b.adventure.entities.container.AbstractContainer;
import di.uniba.map.b.adventure.type.EventType;
import di.uniba.map.b.adventure.type.MutablePlayableRoom;
import di.uniba.map.b.adventure.type.ObjEvent;
import di.uniba.map.b.adventure.type.Room;

public abstract class AbstractEntity {

    private final int id;

    private String name;

    private String description;

    private Set<String> alias;

    private boolean mustDestroyFromInv = false;

    private AbstractEntity parent;

    private List<ObjEvent> events = new ArrayList<>();

    private List<Integer> requiredWearedItemsIdToInteract = new ArrayList<>();

    private List<IWearable> requiredWearedItemsToInteract = new ArrayList<>();

    private String failedInteractionMessage;

    private boolean actionPerformed;

    public boolean isActionPerformed() {
        return actionPerformed;
    }

    public void setActionPerformed(boolean actionPerformed) {
        this.actionPerformed = actionPerformed;
    }

    public String getFailedInteractionMessage() {
        return failedInteractionMessage;
    }

    public void setFailedInteractionMessage(String failedInteractionMessage) {
        this.failedInteractionMessage = failedInteractionMessage;
    }

    public AbstractEntity(int id) {
        this.id = id;
    }

    public AbstractEntity(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public AbstractEntity(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public AbstractEntity(int id, String name, String description, Set<String> alias) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.alias = alias;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<String> getAlias() {
        return alias;
    }

    public void setAlias(Set<String> alias) {
        this.alias = alias;
    }

    public void setAlias(String[] alias) {
        this.alias = new HashSet<>(Arrays.asList(alias));
    }

    public int getId() {
        return this.id;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AbstractEntity other = (AbstractEntity) obj;
        if (id != other.id)
            return false;
        return true;
    }

    public abstract void processReferences(List<AbstractEntity> objects, List<Room> rooms);

    public class Recursive<I> {
        public I func;
    }

    public void processEventReferences(List<AbstractEntity> objects, List<Room> rooms) {
        if (events != null) {
            for (ObjEvent evt : events) {
                for (Room room : rooms) {
                    boolean targetRoomDone = false;
                    boolean parentRoomDone = false;
                    boolean teleportRoomDone = false;

                    if (room instanceof MutablePlayableRoom) {
                        MutablePlayableRoom mRoom = (MutablePlayableRoom) room;

                        if (!targetRoomDone) {
                            if (evt.getUpdateTargetRoomId() != null) {
                                if (evt.getUpdateTargetRoomId() == room.getId()) {
                                    evt.setUpdateTargetRoom(mRoom);
                                    targetRoomDone = true;
                                }
                            } else {
                                targetRoomDone = true;
                            }
                        }

                        if (!parentRoomDone) {
                            if (evt.isUpdatingParentRoom()) {
                                if (mRoom.getObjects().contains(this)) {
                                    evt.setParentRoom(mRoom);
                                    parentRoomDone = true;
                                }

                                if (evt.getParentRoom() == null) {
                                    Recursive<BiFunction<MutablePlayableRoom, ObjEvent, Boolean>> recursive =
                                            new Recursive<>();

                                    recursive.func = (_room, objEvt) -> {
                                        if (objEvt.getParentRoom() == null) {
                                            MutablePlayableRoom nextRoom = _room.getNewRoom();
                                            boolean found = false;

                                            if (nextRoom != null) {
                                                if (nextRoom.getObjects() != null
                                                        && nextRoom.getObjects().contains(this)) {
                                                    found = true;
                                                } else {
                                                    found = recursive.func.apply(_room, objEvt);
                                                }
                                            }
                                            return found;
                                        }
                                        return false;
                                    };

                                    if (mRoom.getNewRoom() != null) {
                                        if (recursive.func.apply(mRoom, evt)) {
                                            evt.setParentRoom(mRoom);
                                            parentRoomDone = true;
                                        }
                                    }
                                }
                            } else {
                                parentRoomDone = true;
                            }
                        }
                    }

                    if (!teleportRoomDone) {
                        if (evt.getTeleportsPlayerToRoomId() != null) {
                            if (evt.getTeleportsPlayerToRoomId() == room.getId()) {
                                evt.setTeleportsPlayerToRoom(room);
                                teleportRoomDone = true;
                            }
                        } else {
                            teleportRoomDone = true;
                        }
                    }

                    if (targetRoomDone && parentRoomDone && teleportRoomDone) {
                        break;
                    }
                }
            }
        }
    }

    public List<ObjEvent> getEvents() {
        return events;
    }

    public ObjEvent getEvent(EventType type) {
        if (getEvents() != null) {
            for (ObjEvent evt : getEvents()) {
                if (evt.getEventType() == type) {
                    if (!evt.isTriggered()) {
                        return evt;
                    }
                }
            }
        }
        return null;
    }

    public StringBuilder processEvent(EventType eventType) {
        ObjEvent evt = getEvent(eventType);

        if (evt != null) {
            if (evt.isUpdatingParentRoom()) {
                evt.getParentRoom().updateToNewRoom();
            }

            if (evt.isUpdatingAnotherRoom()) {
                if (evt.getUpdateTargetRoom() != null) {
                    evt.getUpdateTargetRoom().updateToNewRoom();
                }
            }

            evt.setTriggered(true);

            if (evt.getText() != null && !evt.getText().isEmpty()) {
                return new StringBuilder("<br><br>" + evt.getText());
            }
        }
        return new StringBuilder();
    }

    public boolean isMustDestroyFromInv() {
        return mustDestroyFromInv;
    }

    public void setMustDestroyFromInv(boolean mustDestroyFromInv) {
        this.mustDestroyFromInv = mustDestroyFromInv;
    }

    public AbstractEntity getParent() {
        return parent;
    }

    public void setParent(AbstractEntity parent) {
        this.parent = parent;
    }

    public List<Integer> getRequiredWearedItemsIdToInteract() {
        return requiredWearedItemsIdToInteract;
    }

    public List<IWearable> getRequiredWearedItemsToInteract() {
        return requiredWearedItemsToInteract;
    }

    public StringBuilder getLookMessage() {
        StringBuilder outString = new StringBuilder();

        if (description != null) {
            outString.append(description);
        }

        if (this instanceof IOpenable) {
            IOpenable openableObj = (IOpenable) this;
            if (openableObj.isLocked()) {
                outString.append("È chiuso a chiave.");
            } else if (!openableObj.isOpen()) {
                outString.append("È chiuso.");
            } else if (openableObj.isOpen()) {
                outString.append("È aperto.");
                if (openableObj instanceof AbstractContainer) {
                    AbstractContainer container = (AbstractContainer) openableObj;
                    outString.append(container.revealContent());
                }
            }
        } else if (this instanceof AbstractContainer) {
            AbstractContainer container = (AbstractContainer) this;
            outString.append(container.revealContent());
        } else if (this instanceof IFillable) {
            IFillable fillable = (IFillable) this;
            if (fillable.isFilled()) {
                outString.append("<br>É pieno di: " + fillable.getEligibleItem().getName());
            } else {
                outString.append("<br>È vuoto.");
            }
        }

        if (outString.toString().isEmpty()) {
            outString.append("Nulla di particolare.");
        } else {
            outString.append(processEvent(EventType.LOOK_AT));
        }

        return outString;
    }

    public void setEvents(List<ObjEvent> events) {
        this.events = events;
    }

    public void setRequiredWearedItemsIdToInteract(List<Integer> requiredWearedItemsIdToInteract) {
        this.requiredWearedItemsIdToInteract = requiredWearedItemsIdToInteract;
    }

    public void setRequiredWearedItemsToInteract(List<IWearable> requiredWearedItemsToInteract) {
        this.requiredWearedItemsToInteract = requiredWearedItemsToInteract;
    }

}
