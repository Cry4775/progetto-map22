package di.uniba.map.b.adventure.entities;

import java.util.Set;

// TODO genera gli equals per tutti gli object

public class AdvObjectPushable extends AdvObject implements IPushable {

    private boolean pushed = false;

    public AdvObjectPushable(int id, String name, String description, Set<String> alias) {
        super(id, name, description, alias);
    }

    public AdvObjectPushable(int id) {
        super(id);
    }

    public AdvObjectPushable(int id, String name) {
        super(id, name);
    }

    public AdvObjectPushable(int id, String name, String description) {
        super(id, name, description);
    }

    @Override
    public boolean isPushed() {
        return pushed;
    }

    @Override
    public void setPushed(boolean value) {
        pushed = value;
    }

    @Override
    public boolean push(StringBuilder outString) {
        if (!pushed) {
            pushed = true;

            outString.append("Hai premuto: " + getName());
            // outString.append(handleObjEvent(object.getEvent(EventType.PUSH)));

            return true;
        } else {
            outString.append("È stato già premuto.");
            return false;
        }
    }

}
