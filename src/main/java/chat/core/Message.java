package chat.core;

import java.time.LocalDateTime;
import java.util.Date;

public class Message {

    protected final int id;
    protected final String content;
    protected final LocalDateTime date;
    protected final User user_from;
    protected final Room room;

    public Message(int id, String content, LocalDateTime date, User user_from, Room room) {
        this.id = id;
        this.content = content;
        this.date = date;
        this.user_from = user_from;
        this.room = room;
    }

    public Message(String content, LocalDateTime date, User user_from, Room room){
        this.id = -1;
        this.content = content;
        this.date = date;
        this.user_from = user_from;
        this.room = room;
    }

    public Message withId (int id) {
        return new Message(id, this.content, this.date, this.user_from, this.room);
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
        return user_from;
    }

    public Room getRoom() {
        return room;
    }

    @Override
    public String toString() {
        return "From: " + user_from + " " + date + ">" + content;
    }
}
