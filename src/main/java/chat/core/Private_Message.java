package chat.core;

import java.time.LocalDateTime;

public class Private_Message extends Message{

    private final String user_to;

    public Private_Message(int id, String content, LocalDateTime date, User user_from, Room room, String user_to) {
        super(id, content, date, user_from, room);
        this.user_to = user_to;
    }

    public Private_Message(String content, LocalDateTime date, User user_from, Room room, String user_to){
        super(content, date, user_from, room);
        this.user_to = user_to;
    }

    public Private_Message withId (int id) {
        return new Private_Message(id, super.content, super.date, super.user_from, super.room, this.user_to);
    }

    public int getId() {
        return super.getId();
    }

    public String getContent() {
        return super.getContent();
    }

    public LocalDateTime getDate() {
        return super.getDate();
    }

    public User getUserFrom() {
        return super.getUser();
    }

    public String getUserTo() {
        return user_to;
    }

    public Room getRoom() {
        return super.getRoom();
    }

    @Override
    public String toString() {
        return "From: " + super.getUser().getName() + " " + super.getDate().toLocalTime() + "> " + "(" + user_to + ")" +  super.getContent();
    }
}
