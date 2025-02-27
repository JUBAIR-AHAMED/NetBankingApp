package com.netbanking.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.redisson.api.RedissonClient;

import com.netbanking.util.Redis;
import com.netbanking.util.RedissonHelper;

public class RedissonClientShutdownListener implements ServletContextListener {
    
    private RedissonClient redissonClient = Redis.getInstance();
    private static Logger logger = LogManager.getLogger(RedissonClientShutdownListener.class);
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (redissonClient != null && !redissonClient.isShutdown()) {
        	redissonClient.shutdown();
        	logger.info("RedissonClient is shutdown successfully.");
        }
    }
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
    }
}