package chat.domain;

import chat.core.Message;
import chat.core.Room;
import chat.core.User;

import java.util.ArrayList;

public interface MessageRepo {

    ArrayList<Message> getAllMessagesFromRoom(Room room);

}
