package di.uniba.map.b.adventure.type;

public abstract class AdvEvent {
    private String text;
    private boolean endsTheGame = false;

    // TODO gestire anche l'engine senza file -> costruttori/setter

    public String getText() {
        return text;
    }

    public boolean isEndsTheGame() {
        return endsTheGame;
    }
}
