package com.netbanking.util;

import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class Redis {
    private static final JedisPool jedisPool = new JedisPool(new JedisPoolConfig(), "redis-15838.crce179.ap-south-1-1.ec2.redns.redis-cloud.com", 15838);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String REDIS_PASSWORD = "mgr8R5X0mKULMtR1pH4qDI3RMQYioVaO"; 
    
    private Redis() {}

    private static Jedis getJedis() {
        Jedis jedis = jedisPool.getResource();
        jedis.auth(REDIS_PASSWORD);  // Authenticate
        return jedis;
    }

    public static String get(String cacheKey) {
        try (Jedis jedis = getJedis()) {
            return jedis.get(cacheKey);
        }
    }

    public static void setex(String key, String value) {
        long expiryTime = 3600; //seconds
    	try (Jedis jedis = getJedis()) {
            jedis.setex(key, expiryTime, value);
        }
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
            String cacheGetter = cacheKey + value;
            String json = objectMapper.writeValueAsString(map);
            Redis.setex(cacheGetter, json);
        }
    }

    public static boolean exists(Object keyObj) {
        try (Jedis jedis = getJedis()) {
            return jedis.exists(keyObj.toString());
        }
    }

    public static int delete(Object keyObj) {
        String key = keyObj.toString();
        try (Jedis jedis = getJedis()) {
            return jedis.exists(key) ? (int) jedis.del(key) : -1;
        }
    }
}