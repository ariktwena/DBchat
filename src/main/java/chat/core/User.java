package chat.core;

import java.util.Date;

public class User {

    private final int id;
    private final String name;
//    private final Long date;

    public User(int id, String name) {
        this.id = id;
        this.name = name;
//        this.date = date;
    }

    public User(String name) {
        this.id = -1;
        this.name = name;
//        this.date = date;
    }

//    public static User createUser(String name, Date date){
//        return new User(-1, name, new Date());
//    }

    public User withId (int id) {
        return new User(id, this.name);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

//    public Long getDate() {
//        return date;
//    }

    @Override
    public String toString() {
        return "Username: " + name;
    }
}
