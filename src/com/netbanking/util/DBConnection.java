package com.netbanking.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

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
	
	public static int setValuesInPstm(PreparedStatement pstm, Collection<Object> values, int count) throws SQLException {
		if(values==null || values.isEmpty()) {
			return count;
		}
		for(Object value:values) {
			pstm.setObject(count, value);
			count++;
		}
		return count;
	}
}