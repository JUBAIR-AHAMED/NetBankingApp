package com.netbanking.util;

import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class Redis {
    private static final JedisPool jedisPool = new JedisPool(new JedisPoolConfig(), "127.0.0.1", 6379);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private Redis() {}

    private static Jedis getJedis() {
        return jedisPool.getResource();
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