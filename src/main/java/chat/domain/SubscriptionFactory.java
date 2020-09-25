package chat.domain;

import chat.core.Room;
import chat.core.Subscription;
import chat.core.User;
import chat.infrastructure.DB;

import java.util.ArrayList;

public interface SubscriptionFactory {

    Subscription createSubscriptionForRoom (User user, Room room);
}
