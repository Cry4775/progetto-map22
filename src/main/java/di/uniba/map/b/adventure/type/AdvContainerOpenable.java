/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package di.uniba.map.b.adventure.type;

import java.util.Set;

public class AdvContainerOpenable extends AbstractContainer implements IOpenable {
    private boolean open = false;

    public AdvContainerOpenable(int id) {
        super(id);
    }

    public AdvContainerOpenable(int id, String name) {
        super(id, name);
    }

    public AdvContainerOpenable(int id, String name, String description) {
        super(id, name, description);
    }

    public AdvContainerOpenable(int id, String name, String description, Set<String> alias) {
        super(id, name, description, alias);
    }

    @Override
    public boolean isOpen() {
        return open;
    }

    @Override
    public void setOpen(boolean open) {
        this.open = open;
    }

    @Override
    public boolean open(StringBuilder outString, AdvObject key) {
        if (!open) {
            open = true;
            outString.append("Hai aperto: " + getName());

            outString.append(revealContent());

            // outString.append(handleObjEvent(getEvent(EventType.OPEN_CONTAINER)));

            return true;
        } else if (open) {
            outString.append("É giá aperto. ");

            outString.append(revealContent());
        }
        return false;
    }
}
