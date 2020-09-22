package chat.domain;

import java.util.ArrayList;

public interface UserRepo {

    ArrayList<User> getAllUsers();
    User createUser (User user);
    void deleteUser (User user);
    boolean userExistsInDB (String name);


}
