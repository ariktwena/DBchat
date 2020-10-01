package chat.domain;

import chat.core.User;
import chat.domain.user.UserNotFound;

import java.util.ArrayList;

public interface UserRepo {

    ArrayList<User> getAllUsers(String roomName);
    User getUser(String name) throws UserNotFound;

}
