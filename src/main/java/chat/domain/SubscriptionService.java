package chat.domain;

import chat.core.Room;
import chat.core.User;

public interface SubscriptionService {

    void deleteSubscriptionFromRoom (User user, Room room);
    int NumberOfSubscribersToRoom(Room room);
}
