package com.netbanking.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
	private static String url = "jdbc:mysql://localhost:3306/netbanking?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC";
	private static String username = "root";
	private static String password = "root_pass";
	
	public static Connection getConnection() throws SQLException
	{
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		Connection connection = DriverManager.getConnection(url, username, password);
		return connection;				
	}
}