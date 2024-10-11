package com.netbanking.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
	private static Connection connection=null;
	private static String url = "jdbc:mysql://localhost:3306/netbanking?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC";
	private static String username = "root";
	private static String password = "root_pass";
	
	public static Connection getConnection()
	{
		if(connection == null)
		{
			try
			{
				connection = DriverManager.getConnection(url, username, password);
			}
			catch(SQLException ex)
			{
				ex.printStackTrace();
			}
		}
		return connection;				
	}
}