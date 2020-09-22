package chat.domain;

import chat.infrastructure.DB;

import java.util.ArrayList;

public class MessageFactory implements MessageRepo {

    private final DB db;

    public MessageFactory(DB db) {
        this.db = db;
    }

    @Override
    public ArrayList<Message> getAllMessagesFromRoom(Room room) {
        return null;
    }

    @Override
    public int NumberOfMessagesFromRoom(Room room) {
        return 0;
    }

    @Override
    public Message createMessage(User user, Room room) {
        return null;
    }
}
