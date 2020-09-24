package chat.domain;

import chat.core.Room;

import java.util.ArrayList;

public interface RoomRepo {

    ArrayList<Room> getAllRooms();
    ArrayList<String> getAllRoomNames();
    Room createRoom (Room room);

}
