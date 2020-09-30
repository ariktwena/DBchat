package chat.infrastructure;

import chat.core.*;
import chat.domain.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class DB implements
        UserFactory,
        UserRepo,
        UserService,
        RoomRepo,
        RoomFactory,
        RoomService,
        MessageRepo,
        MessageFactory,
        MessageService,
        SubscriptionRepo,
        SubscriptionFactory,
        SubscriptionService,
        PrivateMessageFactory,
        PrivateMessageService {

    // The entry point of the ChatChad server

    // JDBC driver name and database URL
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost/chat";

    //  Database credentials
    private static final String USER = "testuser";
    private static final String PASS = null;


    // Database version
    private static final int version = 8;

    public DB() {
        if (getCurrentVersion() != getVersion()) {
            throw new IllegalStateException("Database in wrong state, expected:"
                    + getVersion() + ", got: " + getCurrentVersion());
        }
    }

    public static int getCurrentVersion() {
        try (Connection conn = getConnection()) {
            Statement s = conn.createStatement();
            ResultSet rs = s.executeQuery("SELECT value FROM properties WHERE name = 'version';");
            if(rs.next()) {
                String column = rs.getString("value");
                return Integer.parseInt(column);
            } else {
                System.err.println("No version in properties.");
                return -1;
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return -1;
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }

    public static int getVersion() {
        return version;
    }



//
//    public DB() throws ClassNotFoundException {
//        //Load JDBC driver
//        Class.forName(JDBC_DRIVER);
//    }


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
    public boolean userAldreadyExistsInDB(String name) {
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
    public int getUserId(String userName) {

        int user_id = 0;

        try  (Connection connection = getConnection()){
            //Prepare a SQL statement from the DB connection
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT user_id FROM users WHERE user_name = (?);"
            );

            //Link variables to the SQL statement
            ps.setString(1, userName);

            //Execute the SQL query and save the result
            ResultSet rs = ps.executeQuery();

            //Search if there is a result from the DB execution
            if (rs.next()) {

                user_id = rs.getInt("user_id");

                //Return the user
                return user_id;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user_id;
    }

    @Override
    public void userLogin(User user) {
        //Create timeStamp
        LocalDateTime localTime = LocalDateTime.now();

        try  (Connection connection = getConnection()){
            //Prepare a SQL statement from the DB connection
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO logins (log_time, fk_user_id) VALUES (?, ?);"
            );

            //Link variables to the SQL statement
            ps.setTimestamp(1, java.sql.Timestamp.valueOf(localTime));
            ps.setInt(2, user.getId());

            //Execute the SQL statement to update the DB
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void userOnline(User user) {
        //Create timeStamp
        LocalDateTime localTime = LocalDateTime.now();

        try  (Connection connection = getConnection()){
            //Prepare a SQL statement from the DB connection
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO online (online_time, fk_user_id) VALUES (?, ?);"
            );

            //Link variables to the SQL statement
            ps.setTimestamp(1, java.sql.Timestamp.valueOf(localTime));
            ps.setInt(2, user.getId());

            //Execute the SQL statement to update the DB
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
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

    @Override
    public boolean roomExistsInDB(String roomName) {
        try  (Connection connection = getConnection()){
            //Prepare a SQL statement from the DB connection
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT room_name FROM rooms WHERE room_name = (?);"
            );

            //Link variables to the SQL statement
            ps.setString(1, roomName);

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
    public ArrayList<Message> getAllMessagesFromRoom(Room room) {
        return null;
    }

    @Override
    public Message createMessage(Message message) {

        try  (Connection connection = getConnection()){
            //Prepare a SQL statement from the DB connection
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO messages (msg_content, msg_time, fk_user_id, fk_room_id) VALUES (?, ?, ?, ?);",
                    Statement.RETURN_GENERATED_KEYS
            );

            //Link variables to the SQL statement
            ps.setString(1, message.getContent());

            ps.setTimestamp(2, java.sql.Timestamp.valueOf(message.getDate()));

            ps.setInt(3, message.getUser().getId());

            ps.setInt(4, message.getRoom().getId());

            //Execute the SQL statement to update the DB
            ps.executeUpdate();

            //Optional: Get result from the SQL execution, that returns the executed keys (user_id, user_name etc..)
            ResultSet rs = ps.getGeneratedKeys();

            //Search if there is a result from the DB execution
            if (rs.next()) {
                //Create user from the user_id key that is returned form the DB execution
                return message.withId(rs.getInt(1));

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
    public int numberOfMessagesFromRoom(Room room) {
        return 0;
    }

    @Override
    public ArrayList<String> getThe10LastRoomMessages(Room room, User user) {

        //Create array list
        ArrayList<String> messages = new ArrayList<>();

        //Empty
        String broadcastMessage;
        String timeFormat;

        try  (Connection connection = getConnection()){
            //Prepare a SQL statement from the DB connection
            PreparedStatement ps = connection.prepareStatement(
                    "select * from ("
                    + "select "
                    + "messages.msg_time, "
                    + "users.user_name, "
                    + "messages.msg_content "
                    + "from messages "
                    + "inner join users "
                    + "on messages.fk_user_id = users.user_id "
                    + "where fk_room_id = (?) "
                    + " and (?) < msg_time order by messages.msg_time desc limit 10"
                    + ") sub order by msg_time ASC;"
            );

            //Link variables to the SQL statement
            ps.setInt(1, room.getId());
            ps.setTimestamp(2, java.sql.Timestamp.valueOf(user.getDate()));

            //Execute the SQL query and save the result
            ResultSet rs = ps.executeQuery();

            //Search if there is a result from the DB execution
            while (rs.next()) {


                if(rs.getTimestamp("msg_time").toLocalDateTime().getMinute() < 10){
                    timeFormat = rs.getTimestamp("msg_time").toLocalDateTime().getHour() + ":0" + rs.getTimestamp("msg_time").toLocalDateTime().getMinute();
                } else {
                    timeFormat = rs.getTimestamp("msg_time").toLocalDateTime().getHour() + ":" + rs.getTimestamp("msg_time").toLocalDateTime().getMinute();
                }

                broadcastMessage = String.format("%-5s %s %s",
                        timeFormat,
                        rs.getString("user_name") + ":",
                        rs.getString("msg_content"));

                messages.add(broadcastMessage);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return messages;

    }

    @Override
    public Subscription createSubscriptionForRoom(Subscription subscription) {

        try  (Connection connection = getConnection()){
            //Prepare a SQL statement from the DB connection
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO subscriptions (sub_time, fk_user_id, fk_room_id) VALUES (?, ?, ?);",
                    Statement.RETURN_GENERATED_KEYS
            );

            //Link variables to the SQL statement
            ps.setTimestamp(1, java.sql.Timestamp.valueOf(subscription.getDate()));

            ps.setInt(2, subscription.getUser().getId());

            ps.setInt(3, subscription.getRoom().getId());

            //Execute the SQL statement to update the DB
            ps.executeUpdate();

            //Optional: Get result from the SQL execution, that returns the executed keys (user_id, user_name etc..)
            ResultSet rs = ps.getGeneratedKeys();

            //Search if there is a result from the DB execution
            if (rs.next()) {
                //Create user from the user_id key that is returned form the DB execution
                return subscription.withId(rs.getInt(1));

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
    public ArrayList<String> getAllSubscribingUsersFromARoom(Subscription subscription) {
        //Create a user object to store a data in
        ArrayList<String> listOfUsers = new ArrayList<>();

        try  (Connection connection = getConnection()){
            //Prepare a SQL statement from the DB connection
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT DISTINCT users.user_name FROM subscriptions "
                            + "INNER JOIN users "
                            + "ON fk_user_id = user_id "
                            + "WHERE fk_room_id = (?);"
            );

            //Link variables to the SQL statement
            ps.setInt(1, subscription.getRoom().getId());

            //Execute the SQL query and save the result
            ResultSet rs = ps.executeQuery();

            //Search if there is a result from the DB execution
            while (rs.next()) {
                //Create a new user from the DB execution result variables
                listOfUsers.add(rs.getString("user_name"));

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return listOfUsers;
    }

    @Override
    public void deleteSubscriptionFromRoom(User user, Room room) {

    }

    @Override
    public int NumberOfSubscribersToRoom(Room room) {
        return 0;
    }

    @Override
    public Private_Message createPrivateMessage(Private_Message privateMessage, int recipient_id) {

        try  (Connection connection = getConnection()){
            //Prepare a SQL statement from the DB connection
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO private_messages (p_msg_content, p_msg_time, fk_from_user_id, fk_to_user_id, fk_room_id) VALUES (?, ?, ?, ?, ?);",
                    Statement.RETURN_GENERATED_KEYS
            );

            //Link variables to the SQL statement
            ps.setString(1, privateMessage.getContent());

            ps.setTimestamp(2, java.sql.Timestamp.valueOf(privateMessage.getDate()));

            ps.setInt(3, privateMessage.getUser().getId());

            ps.setInt(4, recipient_id);

            ps.setInt(5, privateMessage.getRoom().getId());

            //Execute the SQL statement to update the DB
            ps.executeUpdate();

            //Optional: Get result from the SQL execution, that returns the executed keys (user_id, user_name etc..)
            ResultSet rs = ps.getGeneratedKeys();

            //Search if there is a result from the DB execution
            if (rs.next()) {
                //Create user from the user_id key that is returned form the DB execution
                return privateMessage.withId(rs.getInt(1));

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
    public ArrayList<String> get10NumberOfRoomPrivateMessages(Room room, User user) {
        //Create array list
        ArrayList<String> messages = new ArrayList<>();

        int count = 0;

        //Empty
        String broadcastMessage;
        String timeFormat;

        try  (Connection connection = getConnection()){
            //Prepare a SQL statement from the DB connection
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM ("
                    + "SELECT "
                    + "private_messages.p_msg_time, "
                    + "userFrom.user_name AS 'message from', "
                    + "private_messages.p_msg_content, "
                    + "userTo.user_name AS 'message to' "
                    + "FROM private_messages "
                    + "INNER JOIN users userFrom "
                    + "ON private_messages.fk_from_user_id = userFrom.user_id "
                    + "INNER JOIN users userTo "
                    + "ON private_messages.fk_to_user_id = userTo.user_id "
                    + "WHERE fk_room_id = (?) AND fk_to_user_id = (?) "
                    + "ORDER BY p_msg_time DESC LIMIT 10 "
                    + ") sub ORDER BY p_msg_time ASC;"
            );

            //Link variables to the SQL statement
            ps.setInt(1, room.getId());
            ps.setInt(2, user.getId());

            //Execute the SQL query and save the result
            ResultSet rs = ps.executeQuery();

            //Search if there is a result from the DB execution
            while (rs.next()) {


                if(rs.getTimestamp("p_msg_time").toLocalDateTime().getMinute() < 10){
                    timeFormat = rs.getTimestamp("p_msg_time").toLocalDateTime().getHour() + ":0" + rs.getTimestamp("p_msg_time").toLocalDateTime().getMinute();
                } else {
                    timeFormat = rs.getTimestamp("p_msg_time").toLocalDateTime().getHour() + ":" + rs.getTimestamp("p_msg_time").toLocalDateTime().getMinute();
                }

                broadcastMessage = String.format("%-5s %s %s",
                        timeFormat,
                        rs.getString("message from") + ":",
                        "<private> " + rs.getString("p_msg_content"));

                messages.add(broadcastMessage);

                count ++;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return messages;
    }
}
