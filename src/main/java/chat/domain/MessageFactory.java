package chat.domain;

import chat.core.Message;
import chat.core.Room;
import chat.core.User;
import chat.infrastructure.DB;

import java.util.ArrayList;

public interface MessageFactory {

    ArrayList<Message> getAllMessagesFromRoom(Room room);

}
