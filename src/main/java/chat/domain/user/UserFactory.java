package chat.domain.user;

import chat.core.User;

public interface UserFactory {

    User createUser(User user) throws UserExists;

}
