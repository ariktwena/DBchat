package chat.domain;

import chat.core.Message;
import chat.core.Private_Message;

public interface PrivateMessageFactory {

    Private_Message createPrivateMessage (Private_Message privateMessage, int recipient_id);

}
