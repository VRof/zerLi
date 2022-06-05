package server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 *  connection between database and server
 *
 * <p> Project Name: Zer-Li (Java Application Flower Store) </p>
 *
 * @author Habib Ibrahim, Vitaly Rofman, Ibrahim Daoud, Yosif Hosen
 * @version  V1.00  2022
 */

public class SqlConnector {
	
	private static Connection conn;
	
	public static void main(String[] args) {
		String dbPath = args[0];
		String dbUsername = args[1];
		String dbPass = args[2];
		try 
		{
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();;
            System.out.println("Driver definition succeed");
        } catch (Exception ex) {
        	/* handle the error*/
        	 System.out.println("Driver definition failed");
        	 }
        
        try 
        {
            conn = DriverManager.getConnection("jdbc:mysql://"+ dbPath +"?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Jerusalem",dbUsername,dbPass);
            System.out.println("SQL connection succeed");

     	} catch (SQLException ex) 
     	    {/* handle any errors*/
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            }
   	}
	
	public static Connection getConnection() {
		return conn;
	}
}
