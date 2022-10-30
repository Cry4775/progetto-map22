package component.entity.pickupable;

import component.entity.interfaces.IReadable;

public class ReadableItem extends BasicItem implements IReadable {

    private String readText;

    @Override
    public StringBuilder read() {
        StringBuilder outString = new StringBuilder();

        if (readText != null && !readText.isEmpty()) {
            outString.append(readText);
        } else {
            outString.append("Non c'é scritto nulla.");
        }

        return outString;
    }

}
