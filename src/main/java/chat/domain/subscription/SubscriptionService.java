package chat.domain.subscription;

import chat.core.Room;
import chat.core.User;

public interface SubscriptionService extends SubscriptionRepo{

    void deleteSubscriptionFromRoom (User user, Room room);
    int NumberOfSubscribersToRoom(Room room);
}
