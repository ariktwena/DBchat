package chat.core;

import java.time.LocalDateTime;

public class Subscription {

    private final int id;
    private final LocalDateTime date;
    private final User user;
    private final Room room;

    public Subscription(int id, LocalDateTime date, User user, Room room) {
        this.id = id;
        this.date = date;
        this.user = user;
        this.room = room;
    }

    public Subscription(LocalDateTime date, User user, Room room) {
        this.id = -1;
        this.date = date;
        this.user = user;
        this.room = room;
    }

    public Subscription withId (int id) {
        return new Subscription(id, date, this.user, this.room);
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

    public LocalDateTime getDate() {
        return date;
    }
}
