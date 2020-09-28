package chat.core;

import java.time.LocalDateTime;
import java.util.Date;

public class Message {

    private final int id;
    private final String content;
    private final LocalDateTime date;
    private final User user;
    private final Room room;

    public Message(int id, String content, LocalDateTime date, User user, Room room) {
        this.id = id;
        this.content = content;
        this.date = date;
        this.user = user;
        this.room = room;
    }

    public Message (String content, LocalDateTime date, User user, Room room){
        this.id = -1;
        this.content = content;
        this.date = date;
        this.user = user;
        this.room = room;
    }

    public Message withId (int id) {
        return new Message(id, this.content, this.date, this.user, this.room);
    }

    public int getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public User getUser() {
        return user;
    }

    public Room getRoom() {
        return room;
    }

    @Override
    public String toString() {
        return "From: " + user + " " + date + ">" + content;
    }
}
