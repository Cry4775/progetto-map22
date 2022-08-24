package di.uniba.map.b.adventure.type;

import java.util.Set;

public class AdvObjectMovable extends AdvObject implements IMovable {

    private boolean moved = false;

    public AdvObjectMovable(int id, String name, String description, Set<String> alias) {
        super(id, name, description, alias);
    }

    public AdvObjectMovable(int id) {
        super(id);
    }

    public AdvObjectMovable(int id, String name) {
        super(id, name);
    }

    public AdvObjectMovable(int id, String name, String description) {
        super(id, name, description);
    }

    @Override
    public boolean isMoved() {
        return moved;
    }

    @Override
    public void setMoved(boolean value) {
        moved = value;
    }

    @Override
    public boolean move(StringBuilder outString) {
        if (!moved) {
            moved = true;
            outString.append("Hai spostato: " + getName());

            // outString.append(handleObjEvent(getEvent(EventType.MOVE)));

            return true;
        } else {
            outString.append("È stato già spostato.");
            return false;
        }
    }

}
