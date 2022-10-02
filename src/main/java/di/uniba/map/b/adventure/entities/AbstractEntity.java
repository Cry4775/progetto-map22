package di.uniba.map.b.adventure.entities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import di.uniba.map.b.adventure.entities.container.AbstractContainer;
import di.uniba.map.b.adventure.type.EventType;
import di.uniba.map.b.adventure.type.GameComponent;
import di.uniba.map.b.adventure.type.MutablePlayableRoom;
import di.uniba.map.b.adventure.type.ObjEvent;
import di.uniba.map.b.adventure.type.PlayableRoom;
import di.uniba.map.b.adventure.type.AbstractRoom;

public abstract class AbstractEntity extends GameComponent {

    private Set<String> alias;

    private boolean mustDestroyFromInv = false;

    private GameComponent parent;

    private List<ObjEvent> events = new ArrayList<>();

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

    public abstract void processReferences(List<AbstractEntity> objects, List<AbstractRoom> rooms);

    public void processRoomParent(List<AbstractRoom> rooms) {
        for (AbstractRoom room : rooms) {
            if (room instanceof MutablePlayableRoom) {
                MutablePlayableRoom mRoom = (MutablePlayableRoom) room;

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

    public void processEventReferences(List<AbstractEntity> objects, List<AbstractRoom> rooms) {
        if (events != null) {
            for (ObjEvent evt : events) {
                for (AbstractRoom room : rooms) {
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
                                if (mRoom.getAllObjects().contains(this)) {
                                    evt.setParentRoom(mRoom);
                                    parentRoomDone = true;
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

        if (requiredWearedItemsIdToInteract != null) {
            if (!requiredWearedItemsIdToInteract.isEmpty()) {
                requiredWearedItemsToInteract = new ArrayList<>();

                for (Integer objId : requiredWearedItemsIdToInteract) {
                    for (AbstractEntity obj : objects) {
                        if (obj instanceof IWearable) {
                            if (obj.getId() == objId) {
                                requiredWearedItemsToInteract.add((IWearable) obj);
                                break;
                            }
                        }
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
        StringBuilder outString = new StringBuilder();

        ObjEvent evt = getEvent(eventType);

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
                    // TODO exception
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
                    outString.append(container.revealContent());
                }
            }
        } else if (this instanceof AbstractContainer) {
            AbstractContainer container = (AbstractContainer) this;
            outString.append(container.revealContent());
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
