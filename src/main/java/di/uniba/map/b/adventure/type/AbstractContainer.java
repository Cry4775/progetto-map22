package di.uniba.map.b.adventure.type;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public abstract class AbstractContainer extends AdvItem {

    protected boolean contentRevealed = false;

    private List<AdvObject> list = new ArrayList<>();

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

    public List<AdvObject> getList() {
        return list;
    }

    public void setList(List<AdvObject> list) {
        this.list = list;
    }

    public void add(AdvObject o) {
        list.add(o);
    }

    public void remove(AdvObject o) {
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

            for (AdvObject advObject : getList()) {
                outString.append(" " + advObject.getName() + ",");
            }

            outString.deleteCharAt(outString.length() - 1);

            return outString;
        }
        return new StringBuilder();
    }

    public boolean insert(StringBuilder outString, AdvObject obj, List<AdvObject> inventory) {
        ((AdvItem) obj).setParent(this);
        inventory.remove(obj);

        this.add(obj);

        outString.append("Hai lasciato: " + obj.getName());
        // outString.append(handleObjEvent(obj.getEvent(EventType.INSERT)));

        return true;
    }
}
