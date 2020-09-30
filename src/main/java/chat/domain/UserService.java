package chat.domain;

import chat.core.User;

public interface UserService {

    void deleteUser (User user);
    boolean userAldreadyExistsInDB (String name);
    int getUserId(String userName);
    void userLogin(User user);
    void userOnline(User user);
}
