package chat.infrastructure;

import java.sql.*;

public class DB {

    // The entry point of the ChatChad server

    // JDBC driver name and database URL
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost/chat";
//    private static final String TIME = "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=GMT";
    //private static final String TIME = "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";

    //  Database credentials
    private static final String USER = "testuser";
    private static final String PASS = null;

    private final Connection connection;

    public DB() throws ClassNotFoundException {
        Class.forName(JDBC_DRIVER);
        this.connection = null;
    }

//    public void testDB(){
//        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
//
//            Statement stmt = conn.createStatement();
//            ResultSet rs = stmt.executeQuery("SELECT * FROM users;");
//            while (rs.next()) {
//                System.out.println("id: " + rs.getInt("user_id") + "\nUser name: " + rs.getString("user_name") + "\nRegistration date: " + rs.getTimestamp("user_reg"));
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }


    public Connection getConnection(){

        try {
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
            return connection;
            } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }



//    //Constants
//    private static final String IP	     = "localhost";
//    private static final String PORT     = "3306";
//    public  static final String DATABASE = "chat";
////    public  static final String CEST_TIME_HACK = "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
//    private static final String USERNAME = "testuser";
//    private static final String PASSWORD = null;
//
//    public void DB() throws Exception {
//        Class.forName("com.mysql.cj.jdbc.Driver").newInstance(); //Husk at aktiver denne driver!!!!
//        String url = "jdbc:mysql://" + IP + ":" + PORT + "/" + DATABASE;
//        //String url = "jdbc:mysql://" + IP + ":" + PORT + "/" + DATABASE + "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
//        //String url = "jdbc:mysql://" + IP + ":" + PORT + "/" + DATABASE + "?autoReconnect=true&useSSL=false";
//        this.connection = (Connection) DriverManager.getConnection(url, USERNAME, PASSWORD);
//    }
//
//    public Connection getConnection() {
//        return this.connection;
//    }

//    public void isConnected() throws Exception {
//
//        DBConnector();
//        if(getConnection() == null){
//            System.out.println("DB is null!!");
//        } else {
//            System.out.println("DB is connected :)");
//        }
//    }

//
//    public static void main(String[] args) throws ClassNotFoundException {
//        new DB().testDB();
//    }




}
