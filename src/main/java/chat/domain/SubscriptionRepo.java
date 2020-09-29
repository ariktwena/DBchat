package chat.domain;

import chat.core.Room;
import chat.core.Subscription;
import chat.core.User;

import java.util.ArrayList;

public interface SubscriptionRepo {

    ArrayList<String> getAllSubscribingUsersFromARoom(Subscription subscription);

}
