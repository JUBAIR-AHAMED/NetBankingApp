package com.netbanking.listener;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import com.mysql.cj.jdbc.AbandonedConnectionCleanupThread;
import com.netbanking.util.DBConnectionPool;
import com.zaxxer.hikari.HikariDataSource;

public class DriverCleanupListener implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
	    try {
	        // Unregister JDBC drivers
	        Enumeration<Driver> drivers = DriverManager.getDrivers();
	        while (drivers.hasMoreElements()) {
	            Driver driver = drivers.nextElement();
	            try {
	                DriverManager.deregisterDriver(driver);
	                System.out.println("Deregistered JDBC driver: " + driver);
	            } catch (SQLException e) {
	                System.err.println("Error deregistering driver: " + driver);
	                e.printStackTrace();
	            }
	        }
	        
	        // Stop MySQL cleanup thread to prevent memory leaks
	        AbandonedConnectionCleanupThread.checkedShutdown();
	        System.out.println("MySQL AbandonedConnectionCleanupThread shut down successfully.");
	    } catch (Exception e) {
	        System.err.println("Error shutting down MySQL cleanup thread.");
	        e.printStackTrace();
	    }
	    
        try {
            if (DBConnectionPool.getDataSource() != null) {
                DBConnectionPool.getDataSource().close();
                System.out.println("Database connection pool closed successfully.");
            }
        } catch (Exception e) {
            System.err.println("Error closing database connection pool: " + e.getMessage());
        }

        try {
            if (DBConnectionPool.getDataSource() instanceof HikariDataSource) {
                ((HikariDataSource) DBConnectionPool.getDataSource()).close();
                System.out.println("HikariCP connection pool closed.");
            }
        } catch (Exception e) {
            System.err.println("Error closing HikariCP pool: " + e.getMessage());
        }

	}

    
//    SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
//    SLF4J: Defaulting to no-operation (NOP) logger implementation
//    SLF4J: See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.

    @Override
    public void contextInitialized(ServletContextEvent sce) {
    }
}
