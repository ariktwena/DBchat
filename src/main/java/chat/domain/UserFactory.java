package chat.domain;

import chat.infrastructure.DB;

import java.util.ArrayList;

public class UserFactory implements UserRepo{

    private final DB db;

    public UserFactory(DB db) {
        this.db = db;
    }

    @Override
    public ArrayList<User> getAllUsers() {
        return null;
    }

    @Override
    public User createUser(User user) {
        return null;
    }

    @Override
    public void deleteUser(User user) {

    }

    @Override
    public boolean userExistsInDB(String name) {
        return false;
    }
}
