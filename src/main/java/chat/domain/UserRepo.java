package chat.domain;

import chat.core.User;

import java.util.ArrayList;

public interface UserRepo extends UserFactory{

    ArrayList<User> getAllUsers(String roomName);
    User getUser(String name);

}
