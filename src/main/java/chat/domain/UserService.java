package chat.domain;

import chat.core.User;

public interface UserService {

    void deleteUser (User user);
    boolean userExistsInDB (String name);
    int getUserId(String name);
}
