package test;

import java.io.PrintWriter;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DB {


    public void doSomething() throws Exception {

        //Load DB driver
        //Steps....
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); //Driver 8.0.21
            //Class.forName("com.mysql.jdbc.Driver"); //Driver 5.1.23
            System.out.println("Driver OK!");
        } catch (ClassNotFoundException ex) {
            System.out.println("Can't load DB driver!");
            return;
        }

        System.out.println("Next step...");

        Connection connection = null;

        try {
            //Connect to DB
            System.out.println("Trying DB....");
            //connection = (Connection)DriverManager.getConnection("jdbc:mysql://localhost:3306/netbeans", "TestUser", "1234");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/netbeans?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "TestUser", "1234");
            System.out.println("DB OK!");
        } catch (SQLException ex) {
            System.out.println("Can't connect to 'netbeans' DB!");
            return;
        }


        System.out.println("Connected to DB!!");


        String sql = "SELECT * FROM users WHERE user_id=? AND user_name=?";
        //String sql = "SELECT * FROM users WHERE user_id=? ";

        try {

            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, 1);
            stmt.setString(2, "Arik");

            ResultSet rs = stmt.executeQuery();
            //stmt.close(); //stms skal lukkes, men ikke her, da den blokerer for resten af koden

            //rs.next() vil altid som minimum være 0
            if(rs.next()){
                int user_id = rs.getInt("user_id");
                String user_name = rs.getString("user_name");
                System.out.println("User id is: " + user_id + " and user name is: " + user_name );
            }else {
                System.out.println("Nothing");
            }

            rs.close();

        } catch (SQLException ex) {
            Logger.getLogger(DB.class.getName()).log(Level.SEVERE, null, ex);
        }



        sql = "SELECT count(*) as count FROM users WHERE user_id=? AND user_name=?";
        //String sql = "SELECT * FROM users WHERE user_id=? ";

        try {

            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, 1);
            stmt.setString(2, "Arik");

            ResultSet rs = stmt.executeQuery();
            //stmt.close(); //stms skal lukkes, men ikke her, da den blokerer for resten af koden

            //rs.next() vil altid som minimum være 0
            if(rs.next()){
                int count = rs.getInt("count");
                System.out.println("<br>" + "Count is: " + count );
            }

            rs.close();

        } catch (SQLException ex) {
            Logger.getLogger(DB.class.getName()).log(Level.SEVERE, null, ex);
        }



        sql = "SELECT * FROM users";
        //String sql = "SELECT * FROM users WHERE user_id=? ";

        try {

            Statement stmt = connection.createStatement();

            ResultSet rs = stmt.executeQuery(sql);
            //stmt.close(); //stms skal lukkes, men ikke her, da den blokerer for resten af koden

            //rs.next() vil altid som minimum være 0
            while(rs.next()){
                int user_id = rs.getInt("user_id");
                String user_name = rs.getString("user_name");
                System.out.println("<br>");
                System.out.println("User id is: " + user_id + " and user name is: " + user_name );
            }

            rs.close();

        } catch (SQLException ex) {
            Logger.getLogger(DB.class.getName()).log(Level.SEVERE, null, ex);
        }






        try {
            connection.close();
            System.out.println("DB connection is closed!");
        } catch (SQLException ex) {
            Logger.getLogger(DB.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Can't close DB!");
            return;
        }
    }

    public static void main(String[] args) throws Exception {
        new DB().doSomething();
    }


}
