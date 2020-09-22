package chat.domain;

import java.util.ArrayList;

public interface RoomRepo {

    ArrayList<Room> getAllRooms();
    Room createRoom (Room room);

}
