package com.netbanking.listener;

import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import com.netbanking.activityLogger.AsyncLoggerUtil;
import com.netbanking.util.Executor;

public class ActivityLoggerShutdownListener implements ServletContextListener {
	private static Logger logger = LogManager.getLogger(ActivityLoggerShutdownListener.class);
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Shutting down the executor service gracefully
    	logger.info("Shutting down ActivityLogger executor...");
        Executor executor = Executor.EXECUTOR;
        executor.getInstance().shutdown(); // Initiates the shutdown process
        try {
            if (!executor.getInstance().awaitTermination(20, TimeUnit.SECONDS)) {
                // If the executor did not shut down within the timeout, force shutdown
                executor.getInstance().shutdownNow();
            }
        } catch (InterruptedException e) {
            // If interrupted while waiting for termination, force shutdown
            executor.getInstance().shutdownNow();
            Thread.currentThread().interrupt();  // Preserve the interrupt status
        }
        logger.info("Executor service shut down successfully.");
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // No action needed during initialization
    }
}
