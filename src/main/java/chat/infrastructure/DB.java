package chat.infrastructure;

import java.sql.*;

public class DB {

    // The entry point of the ChatChad server

    // JDBC driver name and database URL
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost/chat";
    private static final String TIME = "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";

    //  Database credentials
    private static final String USER = "testuser";
    private static final String PASS = null;

    public DB() throws ClassNotFoundException {
        Class.forName(JDBC_DRIVER);
    }

    public void testDB(){
        try (Connection conn = DriverManager.getConnection(DB_URL + TIME, USER, null)) {

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM users;");
            while (rs.next()) {
                System.out.println("id: " + rs.getInt("user_id") + "\nUser name: " + rs.getString("user_name") + "\nRegistration date: " + rs.getTimestamp("user_reg"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) throws ClassNotFoundException {
        new DB().testDB();
    }




}
