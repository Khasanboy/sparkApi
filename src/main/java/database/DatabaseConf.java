package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConf {

	static final String JDBC_DRIVER = "org.h2.Driver";
	static final String DB_URL = "jdbc:h2:~/test";

	static final String USER = "sa";
	static final String PASS = "";

	public static void main(String[] args) {
		Connection conn = null;
		Statement stmt = null;
		
		try {
			Class.forName(JDBC_DRIVER);

			System.out.println("Connecting to database...");
			conn = DriverManager.getConnection(DB_URL, USER, PASS);

			System.out.println("Creating table in given database...");
			stmt = conn.createStatement();
		
			//String sql = "DROP SCHEMA searchAPI";
			//stmt.executeUpdate(sql);
			
			
			String sql = "CREATE SCHEMA searchAPI";
			stmt.executeUpdate(sql);
			
			sql= "CREATE TABLE searchAPI.resultFromSearch" + 
					"(resultFromSearchId INTEGER auto_increment, "
					+ "google VARCHAR(255), "+
					"yandex VARCHAR(255))";
			
			stmt.executeUpdate(sql);
			

			sql = "CREATE TABLE searchAPI.searchResult" + 
					"(searchResultId INTEGER auto_increment, " + 
					"queryString VARCHAR(255), " + 
					"statusString VARCHAR(255), "+ 
					"createdDate VARCHAR(255), " + 
					"resultFromSearchId INTEGER, " +
					"FOREIGN KEY (resultFromSearchId) "+ 
					"REFERENCES searchAPI.resultFromSearch(resultFromSearchId))";
			
			stmt.executeUpdate(sql);
			
			
			sql = "CREATE TABLE searchAPI.userInfo" + 
					"(userInfoId INTEGER auto_increment, " + 
					" username VARCHAR(255), "+ 
					" password VARCHAR(255), " + 
					"searchResultId ARRAY)";
					//"FOREIGN KEY (searchResultId) "+ 
					//"REFERENCES searchAPI.searchResult(searchResultId))";
			stmt.executeUpdate(sql);
			
			

			System.out.println("Created table in given database...");

			/*
			 * String sql = "INSERT INTO Registration7 " +
			 * "VALUES (100, 'Zara', 'Ali', 18)";
			 * 
			 * stmt.executeUpdate(sql); sql = "INSERT INTO Registration7 " +
			 * "VALUES (101, 'Mahnaz', 'Fatma', 25)";
			 * 
			 * stmt.executeUpdate(sql); sql = "INSERT INTO Registration7 " +
			 * "VALUES (102, 'Zaid', 'Khan', 30)";
			 * 
			 * stmt.executeUpdate(sql); sql = "INSERT INTO Registration7 " +
			 * "VALUES(103, 'Sumit', 'Mittal', 28)";
			 * 
			 * stmt.executeUpdate(sql);
			 * System.out.println("Inserted records into the table...");
			 * 
			 */
			// STEP 4: Clean-up environment
			stmt.close();
			conn.close();
		} catch (SQLException se) {
			se.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException se2) {
			} 
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}
		} 
	}

}
