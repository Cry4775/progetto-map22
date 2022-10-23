package component.room;

public class CutsceneRoom extends AbstractRoom {
    private AbstractRoom nextRoom;
    private Integer nextRoomId;

    private boolean finalRoom = false;

    public CutsceneRoom(int id, String name, String description, AbstractRoom nextRoom) {
        super(id, name, description);
        this.nextRoom = nextRoom;
    }

    public CutsceneRoom(int id, String name, String description, String imgPath,
            AbstractRoom nextRoom) {
        super(id, name, description, imgPath);
        this.nextRoom = nextRoom;
    }

    public boolean isFinalRoom() {
        return finalRoom;
    }

    public void setFinalRoom(boolean finalRoom) {
        this.finalRoom = finalRoom;
    }

    public AbstractRoom getNextRoom() {
        return nextRoom;
    }

    public void setNextRoom(AbstractRoom nextRoom) {
        this.nextRoom = nextRoom;
    }

    public Integer getNextRoomId() {
        return nextRoomId;
    }

    public void setNextRoomId(int nextRoomId) {
        this.nextRoomId = nextRoomId;
    }
}
