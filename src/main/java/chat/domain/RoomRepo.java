package chat.domain;

import chat.core.Room;

import java.util.ArrayList;

public interface RoomRepo {

    ArrayList<Room> getAllRooms();
    Room createRoom (Room room);

}
