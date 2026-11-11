package ecommercesystem;

import java.sql.*;

public class DBConnection {

    private static final String URL = "jdbc:oracle:thin:@localhost:1521/FREE";
    private static final String USER = "C##ashbiii";
    private static final String PASSWORD = "ashbiii";

    public static Connection getConnection() {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Connection Error: " + e.getMessage());
            return null;
        }
    }
}