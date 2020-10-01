package chat.api;

import chat.core.User;
import chat.domain.UserRepo;
import chat.entries.UserExists;
import chat.infrastructure.DB;

import java.time.LocalDateTime;

public class DBChat {

    private final UserRepo users;
    private final DB db;

    public DBChat(UserRepo users, DB db) {
        this.users = users;
        this.db = db;

    }


    public User createUser(String name, String password, LocalDateTime date) throws UserExists {
        byte[] salt = User.generateSalt();
        byte[] secret = User.calculateSecret(salt, password);
        User user = new User(name, date, salt, secret);
        return db.createUser(user, salt, secret);
    }

    public User login(String name, String password) throws InvalidPassword {
        User user = db.getUser(name);
        if (user.isPasswordCorrect(password)) {
            return user;
        } else  {
//            throw new InvalidPassword();
            return null;
        }
    }
}
