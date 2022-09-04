package di.uniba.map.b.adventure.entities.container;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import di.uniba.map.b.adventure.entities.AbstractEntity;
import di.uniba.map.b.adventure.entities.IFillable;
import di.uniba.map.b.adventure.entities.IFluid;
import di.uniba.map.b.adventure.entities.IPickupable;
import di.uniba.map.b.adventure.type.EventType;
import di.uniba.map.b.adventure.type.Room;

public abstract class AbstractContainer extends AbstractEntity {

    protected boolean contentRevealed = false;

    private List<AbstractEntity> list = new ArrayList<>();

    private boolean forFluids;

    public void setForFluids(boolean forFluids) {
        this.forFluids = forFluids;
    }

    public boolean isForFluids() {
        return forFluids;
    }

    public AbstractContainer(int id) {
        super(id);
    }

    public AbstractContainer(int id, String name) {
        super(id, name);
    }

    public AbstractContainer(int id, String name, String description) {
        super(id, name, description);
    }

    public AbstractContainer(int id, String name, String description, Set<String> alias) {
        super(id, name, description, alias);
    }

    public List<AbstractEntity> getList() {
        return list;
    }

    public void setList(List<AbstractEntity> list) {
        this.list = list;
    }

    public void add(AbstractEntity o) {
        list.add(o);
    }

    public void remove(AbstractEntity o) {
        list.remove(o);
    }

    public boolean isContentRevealed() {
        return contentRevealed;
    }

    public StringBuilder revealContent() {
        if (!contentRevealed) {
            contentRevealed = true;
        }

        if (getList() != null && !getList().isEmpty()) {
            StringBuilder outString = new StringBuilder("<br>Ci trovi:");

            for (AbstractEntity advObject : getList()) {
                outString.append(" " + advObject.getName() + ",");
            }

            outString.deleteCharAt(outString.length() - 1);

            return outString;
        }
        return new StringBuilder();
    }

    public StringBuilder insert(AbstractEntity obj, List<AbstractEntity> inventory) {
        StringBuilder outString = new StringBuilder();

        if (obj instanceof IFluid) {
            if (forFluids) {
                IFluid fluid = (IFluid) obj;

                if (obj.getParent() instanceof IFillable) {
                    IFillable fluidContainer = (IFillable) obj.getParent();

                    fluidContainer.setFilled(false);
                    obj.setParent(this);
                    fluid.setPickedUp(false);
                    this.add(obj);

                    outString.append("Hai versato: " + obj.getName());
                    outString.append(processEvent(EventType.INSERT));

                    setActionPerformed(true);
                }
            } else {
                outString.append("Non puoi versare liquidi qui.");
            }
        } else {
            if (!forFluids) {
                obj.setParent(this);
                inventory.remove(obj);
                ((IPickupable) obj).setPickedUp(false);

                this.add(obj);

                outString.append("Hai lasciato: " + obj.getName());
                outString.append(processEvent(EventType.INSERT));

                setActionPerformed(true);
            } else {
                outString.append("Non puoi lasciare oggetti qui.");
            }
        }

        return outString;
    }

    @Override
    public void processReferences(List<AbstractEntity> objects, List<Room> rooms) {
        if (list != null) {
            for (AbstractEntity item : list) {
                item.setParent(this);
                item.processReferences(objects, rooms);
            }
        } else {
            list = new ArrayList<>();
        }

        processEventReferences(objects, rooms);
    }
}
