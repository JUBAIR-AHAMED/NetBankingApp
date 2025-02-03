package com.netbanking.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.logging.log4j.Level;

import com.netbanking.activityLogger.AsyncLoggerUtil;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DBConnectionPool {

    private static HikariDataSource dataSource;

    static {
    	try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // Ensure driver is loaded
            AsyncLoggerUtil.log(DBConnectionPool.class, Level.INFO, "MySQL JDBC Driver Loaded Successfully");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC Driver Not Found!", e);
        }
    	
        try {
            Properties properties = new Properties();
            InputStream input = DBConnectionPool.class.getClassLoader().getResourceAsStream("dbconfig.properties");
            if (input == null) {
                throw new RuntimeException("dbconfig.properties file not found");
            }
            properties.load(input);

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(properties.getProperty("db.url"));
            config.setUsername(properties.getProperty("db.username"));
            config.setPassword(properties.getProperty("db.password"));
            config.setMaximumPoolSize(Integer.parseInt(properties.getProperty("db.pool.maxSize", "10"))); 
            config.setMinimumIdle(Integer.parseInt(properties.getProperty("db.pool.minIdle", "5")));
            config.setIdleTimeout(Long.parseLong(properties.getProperty("db.pool.idleTimeout", "30000")));
            config.setConnectionTimeout(Long.parseLong(properties.getProperty("db.pool.connectionTimeout", "30000")));

            dataSource = new HikariDataSource(config);
        } catch (Exception e) {
            throw new ExceptionInInitializerError("Failed to initialize DBConnectionPool: " + e.getMessage());
        }
    }
    
    public static HikariDataSource getDataSource() {
        return dataSource;
    }

    public static void shutdown() {
        if (dataSource != null) {
            dataSource.close();
        }
    }
    
    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static void close() {
        if (dataSource != null) {
            dataSource.close();
        }
    }
}
