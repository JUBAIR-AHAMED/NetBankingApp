package com.netbanking.activityLogger;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.netbanking.util.Executor;

public class AsyncLoggerUtil {
    public static void log(Class<?> clazz, Level level, Object message) {
    	Executor executor = Executor.EXECUTOR;
    	executor.getInstance().submit(() -> {
            Logger logger = LogManager.getLogger(clazz);
            if (level == Level.DEBUG) {
                logger.debug(message);
            } else if (level == Level.INFO) {
                logger.info(message);
            } else if (level == Level.WARN) {
                logger.warn(message);
            } else if (level == Level.ERROR) {
                logger.error(message);
            } else if (level == Level.FATAL) {
                logger.fatal(message);
            } else {
                logger.log(level, message);
            }
        });
    }
}