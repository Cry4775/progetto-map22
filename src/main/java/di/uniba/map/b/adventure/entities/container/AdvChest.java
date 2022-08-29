/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package di.uniba.map.b.adventure.entities.container;

import java.util.Set;
import di.uniba.map.b.adventure.entities.AbstractEntity;
import di.uniba.map.b.adventure.entities.IOpenable;
import di.uniba.map.b.adventure.type.EventType;

public class AdvChest extends AbstractContainer implements IOpenable {
    private boolean open = false;

    public AdvChest(int id) {
        super(id);
    }

    public AdvChest(int id, String name) {
        super(id, name);
    }

    public AdvChest(int id, String name, String description) {
        super(id, name, description);
    }

    public AdvChest(int id, String name, String description, Set<String> alias) {
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
    public boolean open(StringBuilder outString, AbstractEntity key) {
        if (!open) {
            open = true;
            outString.append("Hai aperto: " + getName());

            outString.append(revealContent());

            outString.append(processEvent(EventType.OPEN_CONTAINER));

            return true;
        } else if (open) {
            outString.append("É giá aperto. ");

            outString.append(revealContent());
        }
        return false;
    }
}
