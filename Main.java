//package Main;
//
//import java.sql.Connection;
//import java.sql.DriverManager;
//
//public class Main {
//    private static final String URL = "jdbc:mysql://localhost:3306/fitnessmanagementproject";
//    private static final String USER = "root"; // change if yours is different
//    private static final String PASSWORD = "0009"; // add your MySQL password
//
//    public static Connection getConnection() {
//        try {
//            Class.forName("com.mysql.cj.jdbc.Driver"); // for MySQL 8+
//            return DriverManager.getConnection(URL, USER, PASSWORD);
//        } catch (Exception e) {
//            System.out.println("Connection Failed: " + e.getMessage());
//            return null;
//        }
//    }
//}
package Main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Main{
    public static void main(String[] args) throws SQLException, ClassNotFoundException{
     
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection con=DriverManager.getConnection("jdbc:mysql://localhost:3306/fitnessmanagementproject", "root", "0009");
        System.out.println("Connection is successful");
        Statement stmt=con.createStatement();
        
        String str = "select * from client";
        ResultSet rs = stmt.executeQuery(str);
      
        while(rs.next()) {
        	System.out.println("Name is " + rs.getString("Name"));
        	System.out.println("<================================>");
        }
    }
}