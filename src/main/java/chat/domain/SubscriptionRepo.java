package chat.domain;

import chat.core.Room;
import chat.core.Subscription;
import chat.core.User;

import java.util.ArrayList;

public interface SubscriptionRepo {

    ArrayList<User> getAllSubscribingUsersToRoom(Room room);
    int NumberOfSubscribersToRoom(Room room);
    Subscription createSubscriptionForRoom (User user, Room room);
    void deleteSubscriptionFromRoom (User user, Room room);
}
