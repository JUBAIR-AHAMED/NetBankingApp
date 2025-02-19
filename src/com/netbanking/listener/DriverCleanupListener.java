package com.netbanking.listener;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mysql.cj.jdbc.AbandonedConnectionCleanupThread;
import com.netbanking.util.DBConnectionPool;

public class DriverCleanupListener implements ServletContextListener {
	private static Logger logger = LogManager.getLogger(DriverCleanupListener.class);
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		try {
	        // Unregister JDBC drivers
	        Enumeration<Driver> drivers = DriverManager.getDrivers();
	        while (drivers.hasMoreElements()) {
	            Driver driver = drivers.nextElement();
	            try {
	                DriverManager.deregisterDriver(driver);
	                logger.info("Deregistered JDBC driver: " + driver);
	            } catch (SQLException e) {
	            	logger.info("Error deregistering driver: " + driver);
	                e.printStackTrace();
	            }
	        }
	        
	        // Stop MySQL cleanup thread to prevent memory leaks
	        AbandonedConnectionCleanupThread.checkedShutdown();
	        logger.info("MySQL AbandonedConnectionCleanupThread shut down successfully.");
	    } catch (Exception e) {
	    	logger.info("Error shutting down MySQL cleanup thread.");
	        e.printStackTrace();
	    }
	    
        try {
            if (DBConnectionPool.getDataSource() != null) {
                DBConnectionPool.close();
                logger.info("Database connection pool closed successfully.");
            }
        } catch (Exception e) {
        	logger.error("Error closing database connection pool: " + e.getMessage());
        }
	}

    @Override
    public void contextInitialized(ServletContextEvent sce) {
    }
}
