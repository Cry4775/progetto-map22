package di.uniba.map.b.adventure.component.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.google.common.collect.Multimap;
import di.uniba.map.b.adventure.component.GameComponent;
import di.uniba.map.b.adventure.component.entity.container.AbstractContainer;
import di.uniba.map.b.adventure.component.entity.interfaces.IFillable;
import di.uniba.map.b.adventure.component.entity.interfaces.IOpenable;
import di.uniba.map.b.adventure.component.entity.interfaces.IWearable;
import di.uniba.map.b.adventure.component.event.EventType;
import di.uniba.map.b.adventure.component.event.ObjectEvent;
import di.uniba.map.b.adventure.component.room.AbstractRoom;
import di.uniba.map.b.adventure.component.room.MutableRoom;
import di.uniba.map.b.adventure.component.room.PlayableRoom;

public abstract class AbstractEntity extends GameComponent {

    private Set<String> alias;

    private boolean mustDestroyFromInv = false;

    private GameComponent parent;

    private List<ObjectEvent> events = new ArrayList<>();

    private List<Integer> requiredWearedItemsIdToInteract = new ArrayList<>();

    private List<IWearable> requiredWearedItemsToInteract = new ArrayList<>();

    private String failedInteractionMessage;

    private boolean actionPerformed;

    public AbstractEntity(int id, String name, String description) {
        super(id, name, description);
    }

    public AbstractEntity(int id, String name, String description, Set<String> alias) {
        super(id, name, description);
        this.alias = alias;
    }

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

    public Set<String> getAlias() {
        return alias;
    }

    public void setAlias(Set<String> alias) {
        this.alias = alias;
    }

    public void setAlias(String[] alias) {
        this.alias = new HashSet<>(Arrays.asList(alias));
    }

    public abstract void processReferences(Multimap<Integer, AbstractEntity> objects,
            List<AbstractRoom> rooms);

    public void processRoomParent(List<AbstractRoom> rooms) {
        for (AbstractRoom room : rooms) {
            if (room instanceof MutableRoom) {
                MutableRoom mRoom = (MutableRoom) room;

                if (mRoom.getAllObjects().contains(this)) {
                    parent = room;
                }
            } else if (room instanceof PlayableRoom) {
                PlayableRoom playableRoom = (PlayableRoom) room;

                if (playableRoom.getObjects() != null) {
                    if (playableRoom.getObjects().contains(this)) {
                        parent = room;
                    }
                }
            }
        }
    }

    public void processEventReferences(Multimap<Integer, AbstractEntity> objects,
            List<AbstractRoom> rooms) {
        if (events != null) {
            for (ObjectEvent evt : events) {
                boolean targetRoomDone = false;
                boolean parentRoomDone = false;
                boolean teleportRoomDone = false;

                if (evt.isUpdatingParentRoom()) {
                    if (parent instanceof PlayableRoom) {
                        if (parent instanceof MutableRoom) {
                            evt.setParentRoom((MutableRoom) parent);
                            parentRoomDone = true;
                        } else {
                            throw new RuntimeException("Cannot link " + getName() + " ("
                                    + getId()
                                    + ") object event reference. It asks to update its parent room, but it's not a MutableRoom. Check the JSON file.");
                        }
                    }
                }

                for (AbstractRoom room : rooms) {
                    if (!targetRoomDone) {
                        if (evt.getUpdateTargetRoomId() != null) {
                            if (evt.getUpdateTargetRoomId() == room.getId()) {
                                if (room instanceof MutableRoom) {
                                    evt.setUpdateTargetRoom((MutableRoom) room);
                                    targetRoomDone = true;
                                } else {
                                    throw new RuntimeException("Cannot link " + getName() + " ("
                                            + getId()
                                            + ") object event reference. It asks to update a target room, but it's not a MutableRoom. Check the JSON file.");
                                }
                            }
                        } else {
                            targetRoomDone = true;
                        }
                    }

                    if (!parentRoomDone) {
                        if (evt.isUpdatingParentRoom()) {
                            if (room instanceof MutableRoom) {
                                MutableRoom mRoom = (MutableRoom) room;
                                if (mRoom.getAllObjects().contains(this)) {
                                    evt.setParentRoom(mRoom);
                                    parentRoomDone = true;
                                }
                            } else if (room instanceof PlayableRoom) {
                                PlayableRoom pRoom = (PlayableRoom) room;
                                if (pRoom.getObjects().contains(this)) {
                                    throw new RuntimeException("Cannot link " + getName() + " ("
                                            + getId()
                                            + ") object event reference. It asks to update its parent room, but it's not a MutableRoom. Check the JSON file.");
                                }
                            }
                        } else {
                            parentRoomDone = true;
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

                if (!targetRoomDone) {
                    throw new RuntimeException(
                            "Couldn't find the requested \"updateTargetRoom\" event on " + getName()
                                    + " (" + getId()
                                    + "). Check the JSON file for correct room IDs.");
                }

                if (!teleportRoomDone) {
                    throw new RuntimeException(
                            "Couldn't find the requested \"teleportsPlayerToRoom\" event on "
                                    + getName()
                                    + " (" + getId()
                                    + "). Check the JSON file for correct room IDs.");
                }
            }
        }

        if (requiredWearedItemsIdToInteract != null) {
            requiredWearedItemsToInteract = new ArrayList<>();

            for (Integer objId : requiredWearedItemsIdToInteract) {
                for (AbstractEntity reqItem : objects.get(objId)) {
                    if (reqItem instanceof IWearable) {
                        requiredWearedItemsToInteract.add((IWearable) reqItem);
                    }
                }
            }
        }
    }

    public List<ObjectEvent> getEvents() {
        return events;
    }

    public ObjectEvent getEvent(EventType type) {
        if (getEvents() != null) {
            for (ObjectEvent evt : getEvents()) {
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
        StringBuilder outString = new StringBuilder();

        ObjectEvent evt = getEvent(eventType);

        if (evt != null) {
            if (evt.isUpdatingParentRoom()) {
                evt.getParentRoom().updateToNewRoom();
            }

            if (evt.getUpdateTargetRoom() != null) {
                evt.getUpdateTargetRoom().updateToNewRoom();
            }

            if (evt.mustDestroyOnTrigger()) {
                if (parent instanceof AbstractContainer) {
                    AbstractContainer container = (AbstractContainer) parent;

                    container.remove(this);
                } else if (parent instanceof PlayableRoom) {
                    PlayableRoom pRoom = (PlayableRoom) parent;

                    pRoom.removeObject(this);
                } else {
                    throw new RuntimeException(
                            "Couldn't find the parent room of " + getName()
                                    + " (" + getId() + ").");
                }
            }

            if (evt.getText() != null && !evt.getText().isEmpty()) {
                outString.append("\n\n" + evt.getText());
            }

            evt.setTriggered(true);
        }
        return outString;
    }

    public boolean isMustDestroyFromInv() {
        return mustDestroyFromInv;
    }

    public void setMustDestroyFromInv(boolean mustDestroyFromInv) {
        this.mustDestroyFromInv = mustDestroyFromInv;
    }

    public GameComponent getParent() {
        return parent;
    }

    public void setParent(GameComponent parent) {
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

        if (getDescription() != null) {
            outString.append(getDescription());
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
                    outString.append(container.getContentString());
                }
            }
        } else if (this instanceof AbstractContainer) {
            AbstractContainer container = (AbstractContainer) this;
            outString.append(container.getContentString());
        } else if (this instanceof IFillable) {
            IFillable fillable = (IFillable) this;
            if (fillable.isFilled()) {
                outString.append("\nÉ pieno di: " + fillable.getEligibleItem().getName());
            } else {
                outString.append("\nÈ vuoto.");
            }
        }

        if (outString.toString().isEmpty()) {
            outString.append("Nulla di particolare.");
        } else {
            outString.append(processEvent(EventType.LOOK_AT));
        }

        return outString;
    }

    public void setEvents(List<ObjectEvent> events) {
        this.events = events;
    }

    public void setRequiredWearedItemsIdToInteract(List<Integer> requiredWearedItemsIdToInteract) {
        this.requiredWearedItemsIdToInteract = requiredWearedItemsIdToInteract;
    }

    public void setRequiredWearedItemsToInteract(List<IWearable> requiredWearedItemsToInteract) {
        this.requiredWearedItemsToInteract = requiredWearedItemsToInteract;
    }

}
