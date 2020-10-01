package chat.domain.user;

public class UserAlreadyLoggedIn extends Exception {

    public UserAlreadyLoggedIn(String name) {
        super("User already logged in: " + name);
    }
}
