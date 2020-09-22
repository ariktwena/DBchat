package chat.domain;

import java.util.Date;

public class User {

    private final int id;
    private final String name;
    private final Date date;

    public User(int id, String name, Date date) {
        this.id = id;
        this.name = name;
        this.date = date;
    }

    public static User createUser(String name, Date date){
        return new User(-1, name, new Date());
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

    public Date getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "Username: " + name;
    }
}
