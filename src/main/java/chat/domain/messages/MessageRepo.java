package chat.domain.messages;

import chat.core.Message;
import chat.core.Room;
import chat.core.User;

import java.util.ArrayList;

public interface MessageRepo extends MessageFactory {

    ArrayList<Message> getAllMessagesFromRoom(Room room);

}
