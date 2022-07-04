package di.uniba.map.b.adventure.type;

public class NonPlayableRoom extends Room {
    private Room nextRoom;
    private Integer nextRoomId;

    public NonPlayableRoom(int id, String name, String description, Room nextRoom) {
        super(id, name, description);
        this.nextRoom = nextRoom;
    }

    public NonPlayableRoom(int id, String name, String description, String imgPath, Room nextRoom) {
        super(id, name, description, imgPath);
        this.nextRoom = nextRoom;
    }

    public Room getNextRoom() {
        return nextRoom;
    }

    public void setNextRoom(Room nextRoom) {
        this.nextRoom = nextRoom;
    }

    public Integer getNextRoomId() {
        return nextRoomId;
    }

    public void setNextRoomId(int nextRoomId) {
        this.nextRoomId = nextRoomId;
    }
}
