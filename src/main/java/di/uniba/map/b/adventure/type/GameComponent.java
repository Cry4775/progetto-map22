package di.uniba.map.b.adventure.type;

public abstract class GameComponent {
    private final int id;

    private String name;

    private String description;

    public GameComponent(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public GameComponent(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        GameComponent other = (GameComponent) obj;
        if (id != other.id)
            return false;
        return true;
    }
}
