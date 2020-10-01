package chat.domain.room;

import chat.core.Room;

import java.util.ArrayList;

public interface RoomRepo extends RoomFactory{

    ArrayList<Room> getAllRooms();
    ArrayList<String> getAllRoomNames();
    Room getRoom (String roomName);

}
