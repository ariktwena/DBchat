package chat.domain;

import chat.core.Room;
import chat.core.User;

import java.util.ArrayList;

public interface PrivateMessageService {

    ArrayList<String> get10NumberOfRoomPrivateMessages(Room room, User user);
}
