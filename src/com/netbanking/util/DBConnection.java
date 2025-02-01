package com.netbanking.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {
	private static String url = "jdbc:mysql://localhost:3306/netbanking?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC";
	private static String username = "root";
	private static String password = "root_pass";

    static {
    	try (InputStream input = DBConnection.class.getClassLoader().getResourceAsStream("dbconfig.properties")) {
            if (input == null) {
                throw new RuntimeException("Unable to find config.properties file.");
            }

            Properties properties = new Properties();
            properties.load(input);

            url = properties.getProperty("db.url");
            username = properties.getProperty("db.username");
            password = properties.getProperty("db.password");
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize database connection.");
        }
    }

    public static Connection getConnection() throws SQLException {
    	try {
    		Class.forName("com.mysql.cj.jdbc.Driver");
    	} catch (ClassNotFoundException e) {
    		e.printStackTrace();
    		throw new RuntimeException("Database driver not found.");
    	}
    	Connection connection = DriverManager.getConnection(url, username, password);
        return connection;
    }
}