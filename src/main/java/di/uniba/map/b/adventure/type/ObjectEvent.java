package di.uniba.map.b.adventure.type;

public class ObjectEvent {
    private CommandType type;
    private int roomId;
    private int objectId;
    private String text;
    private boolean oneTime = false;
    private boolean endsTheGame = false;

    // TODO gestire anche l'engine senza file -> costruttori/setter

    public CommandType getType() {
        return type;
    }

    public int getRoomId() {
        return roomId;
    }

    public int getObjectId() {
        return objectId;
    }

    public String getText() {
        return text;
    }

    public boolean isOneTime() {
        return oneTime;
    }

    public boolean isEndsTheGame() {
        return endsTheGame;
    }
}
