package di.uniba.map.b.adventure.entities.container;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import di.uniba.map.b.adventure.entities.AbstractEntity;
import di.uniba.map.b.adventure.type.EventType;

public abstract class AbstractContainer extends AbstractEntity {

    protected boolean contentRevealed = false;

    private List<AbstractEntity> list = new ArrayList<>();

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

    public boolean insert(StringBuilder outString, AbstractEntity obj,
            List<AbstractEntity> inventory) {
        obj.setParent(this);
        inventory.remove(obj);

        this.add(obj);

        outString.append("Hai lasciato: " + obj.getName());
        outString.append(processEvent(EventType.INSERT));

        return true;
    }
}