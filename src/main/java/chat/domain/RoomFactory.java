package chat.domain;

import chat.core.Room;
import chat.infrastructure.DB;

import java.util.ArrayList;

public class RoomFactory implements RoomRepo{

    private final DB db;

    public RoomFactory(DB db) {
        this.db = db;
    }

    @Override
    public ArrayList<Room> getAllRooms() {
        return null;
    }

    @Override
    public ArrayList<String> getAllRoomNames() {
        return null;
    }

    @Override
    public Room createRoom(Room room) {
        return null;
    }
}
