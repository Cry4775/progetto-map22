package di.uniba.map.b.adventure.entities;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class AdvPerson extends AbstractEntity implements ITalkable {

    private Queue<String> phrases = new LinkedList<>();

    public AdvPerson(int id) {
        super(id);
    }

    public AdvPerson(int id, String name) {
        super(id, name);
    }

    public AdvPerson(int id, String name, String description) {
        super(id, name, description);
    }

    public AdvPerson(int id, String name, String description, Set<String> alias) {
        super(id, name, description, alias);
    }

    @Override
    public boolean talk(StringBuilder outString) {
        if (phrases != null) {
            if (!phrases.isEmpty()) {
                if (phrases.size() > 1) {
                    outString.append(phrases.poll());
                } else {
                    outString.append(phrases.peek());
                }
            } else {
                outString.append("...");
            }
        } else {
            outString.append("...");
        }

        return true;
    }
}
