package chat.domain;

import chat.core.User;
import chat.infrastructure.DB;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
        try  {
            PreparedStatement ps = db.getConnection().prepareStatement(
                    "INSERT INTO users (user_name) VALUES (?);",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, user.getName());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return user.withId(rs.getInt(1));
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }





    @Override
    public void deleteUser(User user) {

    }

    @Override
    public boolean userExistsInDB(String name) {
        try  {
            PreparedStatement ps = db.getConnection().prepareStatement(
                    "SELECT user_name FROM users WHERE user_name = (?);",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public User getUser(String name) {
        User user = null;

        try  {
            PreparedStatement ps = db.getConnection().prepareStatement(
                    "SELECT * FROM users WHERE user_name = (?);",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {

                user = new User(rs.getInt("user_id"), rs.getString("user_name"));

                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }
}
