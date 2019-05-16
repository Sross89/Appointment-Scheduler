/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.sql.Connection;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author simon
 */
public class DBConnection {
    
    private static final String dbName = "U05MHQ";
    private static final String dbURL = "jdbc:mysql://52.206.157.109/" + dbName;
    private static final String username = "U05MHQ";
    private static final String password = "53688542762";
    private static final String driver = "com.mysql.cj.jdbc.Driver";
    public static Connection conn;
    
    public static void makeConnection() throws ClassNotFoundException, SQLException
    {
        Class.forName(driver);
        conn = (Connection) DriverManager.getConnection(dbURL, username, password);
        System.out.println("Connection successful!");
    }
    
    public static void closeConnection() throws SQLException, IOException
    {
        conn.close();
        System.out.println("Connection closed!");
    }
}
