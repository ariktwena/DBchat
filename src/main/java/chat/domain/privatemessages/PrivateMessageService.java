package chat.domain.privatemessages;

import chat.core.Room;
import chat.core.User;

import java.util.ArrayList;

public interface PrivateMessageService extends PrivateMessageFactory{

    ArrayList<String> get10NumberOfRoomPrivateMessages(Room room, User user);
}
