package chat.domain.messages;

import chat.core.Message;
import chat.core.Room;
import chat.core.User;
import chat.infrastructure.DB;

import java.util.ArrayList;

public interface MessageFactory {

    Message createMessage (Message message);

}
