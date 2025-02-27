package com.netbanking.util;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netbanking.activityLogger.AsyncLoggerUtil;

import org.apache.logging.log4j.Level;
import org.redisson.Redisson;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

public class Redis {

    private static RedissonClient redissonClient;
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String REDIS_HOST = "redis-17771.c330.asia-south1-1.gce.redns.redis-cloud.com";
    private static final int REDIS_PORT = 17771;
    private static final String REDIS_PASSWORD = "jhCT45E2lvchvuw0MDQ9UMeEX7IOkGSm";

    static {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://" + REDIS_HOST + ":" + REDIS_PORT)
                .setPassword(REDIS_PASSWORD)
                .setConnectionPoolSize(10)
                .setConnectionMinimumIdleSize(1)
                .setIdleConnectionTimeout(2000);
        try {
        	redissonClient = Redisson.create(config);
        	AsyncLoggerUtil.log(Redis.class, Level.ERROR, "Redis connection established successfully.");
        } catch (Exception e) {
        	AsyncLoggerUtil.log(Redis.class, Level.ERROR, e);
        	e.printStackTrace();
		}
    }

    private Redis() {}
    
    public static RedissonClient getInstance() {
        return redissonClient;
    }
    
    public static String get(String cacheKey) {
        RBucket<String> bucket = redissonClient.getBucket(cacheKey);
        return bucket.get();
    }

    public static void setex(String key, String value) {
        long expiryTime = 3600; // seconds
        RBucket<String> bucket = redissonClient.getBucket(key);
        Duration duration = Duration.ofSeconds(expiryTime);
        bucket.set(value, duration);
    }

    public static <T> void setex(String key, T value) throws JsonProcessingException {
        if (exists(key)) {
            return;
        }
        String stringValue = objectMapper.writeValueAsString(value);
        setex(key, stringValue);
    }

    public static void setList(String cacheKey, String valueKey, List<Map<String, Object>> list) throws JsonProcessingException {
        for (Map<String, Object> map : list) {
            Long value = (Long) map.get(valueKey);
            if (value != null) {
                String cacheGetter = cacheKey + value;
                String json = objectMapper.writeValueAsString(map);
                setex(cacheGetter, json);
            }
        }
    }

    public static boolean exists(String key) {
        return redissonClient.getBucket(key).isExists();
    }

    public static int delete(String key) {
        return redissonClient.getBucket(key).delete() ? 1 : -1;
    }

    public static void shutdown() {
        redissonClient.shutdown();
    }
}
