package chat.domain;

import chat.infrastructure.DB;

import java.util.ArrayList;

public class SubscriptionFactory implements SubscriptionRepo{

    private final DB db;

    public SubscriptionFactory(DB db) {
        this.db = db;
    }

    @Override
    public ArrayList<User> getAllSubscribingUsersToRoom(Room room) {
        return null;
    }

    @Override
    public int NumberOfSubscribersToRoom(Room room) {
        return 0;
    }

    @Override
    public Subscription createSubscriptionForRoom(User user, Room room) {
        return null;
    }

    @Override
    public void deleteSubscriptionFromRoom(User user, Room room) {

    }
}
