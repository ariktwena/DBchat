package chat.core;

import java.security.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

public class User {

    private final int id;
    private final String name;
    private final LocalDateTime date;

    public User(int id, String name, LocalDateTime date) {
        this.id = id;
        this.name = name;
        this.date = date;
    }

    public User(String name, LocalDateTime date) {
        this.id = -1;
        this.name = name;
        this.date = date;
    }

    public User withId (int id) {
        return new User(id, this.name, this.date);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "Username: " + name;
    }
}
