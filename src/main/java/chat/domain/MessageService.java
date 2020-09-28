package chat.domain;

import chat.core.Room;
import chat.core.User;

import java.util.ArrayList;

public interface MessageService {

    int numberOfMessagesFromRoom(Room room);
    ArrayList<String> getThe10LastRoomMessages(Room room, User user);
}
