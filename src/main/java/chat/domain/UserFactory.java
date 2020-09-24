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
    public ArrayList<User> getAllUsers(String roomName) {
        return null;
    }

    @Override
    public User createUser(User user) {
        try  {
            //Prepare a SQL statement from the DB connection
            PreparedStatement ps = db.getConnection().prepareStatement(
                    "INSERT INTO users (user_name) VALUES (?);",
                    Statement.RETURN_GENERATED_KEYS
            );

            //Link variables to the SQL statement
            ps.setString(1, user.getName());

            //Execute the SQL statement to update the DB
            ps.executeUpdate();

            //Optional: Get result from the SQL execution, that returns the executed keys (user_id, user_name etc..)
            ResultSet rs = ps.getGeneratedKeys();

            //Search if there is a result from the DB execution
            if (rs.next()) {
                //Create user from the user_id key that is returned form the DB execution
                return user.withId(rs.getInt(1));

            } else {
                //Return null, if no result is returned form the execution
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
            //Prepare a SQL statement from the DB connection
            PreparedStatement ps = db.getConnection().prepareStatement(
                    "SELECT user_name FROM users WHERE user_name = (?);",
                    Statement.RETURN_GENERATED_KEYS
            );

            //Link variables to the SQL statement
            ps.setString(1, name);

            //Execute the SQL query and save the result
            ResultSet rs = ps.executeQuery();

            //Search if there is a result from the DB execution
            if (rs.next()) {
                //Return true, if the is a DB execution result
                return true;

            } else {
                //Return false, if the isn't a DB execution result
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public User getUser(String name) {
        //Create a user object to store a data in
        User user = null;

        try  {
            //Prepare a SQL statement from the DB connection
            PreparedStatement ps = db.getConnection().prepareStatement(
                    "SELECT * FROM users WHERE user_name = (?);",
                    Statement.RETURN_GENERATED_KEYS
            );

            //Link variables to the SQL statement
            ps.setString(1, name);

            //Execute the SQL query and save the result
            ResultSet rs = ps.executeQuery();

            //Search if there is a result from the DB execution
            if (rs.next()) {
                //Create a new user from the DB execution result variables
                user = new User(rs.getInt("user_id"), rs.getString("user_name"));

                //Return the user
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }

    @Override
    public int getUserId(String name) {
        return 0;
    }
}
