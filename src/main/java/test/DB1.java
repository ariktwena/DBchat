package test;

import java.sql.Connection;
import java.sql.DriverManager;

public class DB1 {

    private Connection connection = null;

    //Constants
    private static final String IP	     = "localhost";
    private static final String PORT     = "3306";
    public  static final String DATABASE = "netbeans";
    public  static final String CEST_TIME_HACK = "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
    private static final String USERNAME = "TestUser";
    private static final String PASSWORD = "1234";

    public void DBConnector() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver").newInstance(); //Husk at aktiver denne driver!!!!
        String url = "jdbc:mysql://" + IP + ":" + PORT + "/" + DATABASE + CEST_TIME_HACK;
        //String url = "jdbc:mysql://" + IP + ":" + PORT + "/" + DATABASE + "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
        //String url = "jdbc:mysql://" + IP + ":" + PORT + "/" + DATABASE + "?autoReconnect=true&useSSL=false";
        this.connection = (Connection) DriverManager.getConnection(url, USERNAME, PASSWORD);
    }

    public Connection getConnection() {
        return this.connection;
    }

    public void isConnected() throws Exception {

        DBConnector();
        if(getConnection() == null){
            System.out.println("DB is null!!");
        } else {
            System.out.println("DB is connected :)");
        }
    }

    public static void main(String[] args) throws Exception {
        new DB1().isConnected();
    }

}
