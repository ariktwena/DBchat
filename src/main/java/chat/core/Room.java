package chat.core;

public class Room {

    private final int id;
    private final String name;

    public Room(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Room(String name) {
        this.id = -1;
        this.name = name;
    }

    public Room withId (int id) {
        return new Room(id, this.name);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Room name: " + name;
    }
}
