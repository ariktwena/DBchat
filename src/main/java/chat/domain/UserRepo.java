package chat.domain;

import chat.core.User;

import java.util.ArrayList;

public interface UserRepo {

    ArrayList<User> getAllUsers(String roomName);
    User createUser (User user);
    void deleteUser (User user);
    boolean userExistsInDB (String name);
    User getUser(String name);
    int getUserId(String name);


}
