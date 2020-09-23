package chat.core;

public class Subscription {

    private final int id;
    private final User user;
    private final Room room;

    public Subscription(int id, User user, Room room) {
        this.id = id;
        this.user = user;
        this.room = room;
    }

    public static Subscription createSubscription(User user, Room room){
        return new Subscription(-1, user, room);
    }

    public Subscription withId (int id) {
        return new Subscription(id, this.user, this.room);
    }

    public int getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Room getRoom() {
        return room;
    }
}
