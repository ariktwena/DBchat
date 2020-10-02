package chat.domain.subscription;

import chat.core.Room;
import chat.core.Subscription;
import chat.core.User;

import java.util.ArrayList;

public interface SubscriptionRepo extends SubscriptionFactory {

    ArrayList<String> getAllSubscribingUsersFromARoom(Subscription subscription);

}
