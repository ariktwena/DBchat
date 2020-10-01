package chat.api;

import chat.core.User;
import chat.domain.user.InvalidPassword;
import chat.domain.user.UserAlreadyLoggedIn;
import chat.domain.user.UserNotFound;
import chat.infrastructure.DB;

import java.util.HashSet;
import java.util.Set;

public class DBChat {
    private final DB db;

    private Set<User> activeUsers = new HashSet<>();

    public DBChat(DB db) {
        this.db = db;
    }

    public synchronized User login(String name, String password)
            throws UserNotFound, InvalidPassword, UserAlreadyLoggedIn {
       // Login code
        User user = db.getUser(name);
        if (activeUsers.contains(user)) {
            throw new UserAlreadyLoggedIn(name);
        } else {
            activeUsers.add(user);
            db.userLogin(user);
            db.userOnline(user);
            return user;
        }
    }

    public synchronized User createUserAndLogIn(String name, String password)
    {
        // create and loggin
        return null;
    }

    public synchronized void logoff(User user) {
        // Logoff code
        activeUsers.remove(user);
    }



}
