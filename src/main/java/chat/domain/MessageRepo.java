package chat.domain;

import java.util.ArrayList;

public interface MessageRepo {

    ArrayList<Message> getAllMessagesFromRoom(Room room);
    int NumberOfMessagesFromRoom(Room room);
    Message createMessage (User user, Room room);

}
