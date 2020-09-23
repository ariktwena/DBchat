package chat.core;

import java.util.Date;

public class Message {

    private final int id;
    private final String content;
    private final Date date;
    private final User from;
    private final Room room;

    public Message(int id, String content, Date date, User from, Room room) {
        this.id = id;
        this.content = content;
        this.date = date;
        this.from = from;
        this.room = room;
    }

    public static Message createMessage(String content, Date date, User from, Room room){
        return new Message(-1, content, date, from, room);
    }

    public Message withId (int id) {
        return new Message(id, this.content, this.date, this.from, this.room);
    }

    public int getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public Date getDate() {
        return date;
    }

    public User getFrom() {
        return from;
    }

    public Room getRoom() {
        return room;
    }

    @Override
    public String toString() {
        return "From: " + from + " " + date + ">" + content;
    }
}
