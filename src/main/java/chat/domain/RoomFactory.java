package chat.domain;

import chat.core.Room;
import chat.infrastructure.DB;

import java.util.ArrayList;

public interface RoomFactory{

    Room createRoom (Room room);

}
