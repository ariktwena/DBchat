package chat.infrastructure;

import java.sql.*;

public class DB {

    // The entry point of the ChatChad server

    // JDBC driver name and database URL
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost/chat";

    //  Database credentials
    private static final String USER = "testuser";
    private static final String PASS = null;

    //Connection state
    private final Connection connection;

    public DB() throws ClassNotFoundException {
        //Load JDBC driver
        Class.forName(JDBC_DRIVER);
        //Set connection to null, so we can test if there is a connection
        this.connection = null;
    }

    //Method to get a the DB connection
    public Connection getConnection(){
        try {
            //Create connection
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);

            //Return the connection
            return connection;
            } catch (SQLException e) {
            e.printStackTrace();
        }

        //Return null if connection fails
        return null;
    }

}
