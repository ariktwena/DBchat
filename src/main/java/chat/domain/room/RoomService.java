package chat.domain.room;

public interface RoomService extends RoomRepo {

    boolean roomExistsInDB (String roomName);
}
