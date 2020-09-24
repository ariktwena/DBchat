package chat.infrastructure;

import chat.core.Room;
import chat.core.User;
import chat.domain.*;

import java.sql.*;
import java.util.ArrayList;

public class DB implements UserFactory, UserRepo, UserService, RoomRepo, RoomFactory, RoomService {

    // The entry point of the ChatChad server

    // JDBC driver name and database URL
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost/chat";

    //  Database credentials
    private static final String USER = "testuser";
    private static final String PASS = null;

    public DB() throws ClassNotFoundException {
        //Load JDBC driver
        Class.forName(JDBC_DRIVER);
    }

    //Get DB connection
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, null);
    }

    @Override
    public User createUser(User user) {
        try  (Connection connection = getConnection()){
            //Prepare a SQL statement from the DB connection
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO users (user_name, user_reg) VALUES (?, ?);",
                    Statement.RETURN_GENERATED_KEYS
            );

            //Link variables to the SQL statement
            ps.setString(1, user.getName());
            ps.setTimestamp(2, java.sql.Timestamp.valueOf(user.getDate()));

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
    public ArrayList<User> getAllUsers(String roomName) {
        return null;
    }

    @Override
    public void deleteUser(User user) {

    }

    @Override
    public boolean userExistsInDB(String name) {
        try  (Connection connection = getConnection()){
            //Prepare a SQL statement from the DB connection
            PreparedStatement ps = connection.prepareStatement(
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
    public User getUser(String userName) {
        //Create a user object to store a data in
        User user = null;

        try  (Connection connection = getConnection()){
            //Prepare a SQL statement from the DB connection
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM users WHERE user_name = (?);"
            );

            //Link variables to the SQL statement
            ps.setString(1, userName);

            //Execute the SQL query and save the result
            ResultSet rs = ps.executeQuery();

            //Search if there is a result from the DB execution
            if (rs.next()) {
                //Create a new user from the DB execution result variables
                user = new User(rs.getInt("user_id"), rs.getString("user_name"), rs.getTimestamp("user_reg").toLocalDateTime());

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

    @Override
    public Room createRoom(Room room) {
        try  (Connection connection = getConnection()){
            //Prepare a SQL statement from the DB connection
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO rooms (room_name) VALUES (?);",
                    Statement.RETURN_GENERATED_KEYS
            );

            //Link variables to the SQL statement
            ps.setString(1, room.getName());

            //Execute the SQL statement to update the DB
            ps.executeUpdate();

            //Optional: Get result from the SQL execution, that returns the executed keys (user_id, user_name etc..)
            ResultSet rs = ps.getGeneratedKeys();

            //Search if there is a result from the DB execution
            if (rs.next()) {
                //Create user from the user_id key that is returned form the DB execution
                return room.withId(rs.getInt(1));

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
    public ArrayList<Room> getAllRooms() {
        return null;
    }

    @Override
    public ArrayList<String> getAllRoomNames() {
        //Create a user object to store a data in
        ArrayList<String> listOfRoomNames = new ArrayList<>();

        try  (Connection connection = getConnection()){
            //Prepare a SQL statement from the DB connection
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT room_name FROM rooms; "
            );

            //Execute the SQL query and save the result
            ResultSet rs = ps.executeQuery();

            //Search if there is a result from the DB execution
            while (rs.next()) {
                //Create a new user from the DB execution result variables
                listOfRoomNames.add(rs.getString("room_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return listOfRoomNames;
    }

    @Override
    public Room getRoom(String roomName) {
        //Create a user object to store a data in
        Room room = null;

        try  (Connection connection = getConnection()){
            //Prepare a SQL statement from the DB connection
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM rooms WHERE room_name = (?);"
            );

            //Link variables to the SQL statement
            ps.setString(1, roomName);

            //Execute the SQL query and save the result
            ResultSet rs = ps.executeQuery();

            //Search if there is a result from the DB execution
            if (rs.next()) {
                //Create a new user from the DB execution result variables
                room = new Room(rs.getInt("room_id"), rs.getString("room_name"));

                //Return the user
                return room;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return room;
    }
}
