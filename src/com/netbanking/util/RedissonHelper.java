package com.netbanking.util;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

public class RedissonHelper {
    private static RedissonClient redisson;

    static {
        Config config = new Config();

        // Read environment variables
        String redisHost = System.getenv("REDIS_HOST");
        String redisPort = System.getenv("REDIS_PORT");
        String redisPassword = System.getenv("REDIS_PASSWORD");

        // Fallback defaults if environment variables are not set
        if (redisHost == null || redisHost.trim().isEmpty()) {
            redisHost = "redis-15838.crce179.ap-south-1-1.ec2.redns.redis-cloud.com";
        }
        if (redisPort == null || redisPort.trim().isEmpty()) {
            redisPort = "15838";
        }
        if (redisPassword == null || redisPassword.trim().isEmpty()) {
            redisPassword = "mgr8R5X0mKULMtR1pH4qDI3RMQYioVaO";
        }

        String redisAddress = "redis://" + redisHost + ":" + redisPort;
        config.useSingleServer().setAddress(redisAddress)
              .setPassword(redisPassword);
        redisson = Redisson.create(config);
        System.out.println("conf "+config);
    }

    public static RedissonClient getInstance() {
        return redisson;
    }
}
