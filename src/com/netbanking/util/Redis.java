package com.netbanking.util;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.resps.ScanResult;

public class Redis {
    private static final JedisPool jedisPool = new JedisPool(new JedisPoolConfig(), "localhost", 6379);
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
        try (Jedis jedis = getJedis()) {
            jedis.setex(key, 3600, value);
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
        try (Jedis jedis = getJedis()) {
            for (Map<String, Object> map : list) {
                Long value = (Long) map.get(valueKey);
                String cacheGetter = cacheKey + value;
                String json = objectMapper.writeValueAsString(map);
                jedis.setex(cacheGetter, 3600, json);
            }
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

    public static void deleteKeysWithStartString(String cacheKey) {
        try (Jedis jedis = getJedis()) {
            String cursor = "0";
            do {
                ScanResult<String> result = jedis.scan(cursor, 
                        new redis.clients.jedis.params.ScanParams().match(cacheKey + "*").count(100));
                cursor = result.getCursor();
                for (String key : result.getResult()) {
                    jedis.del(key);
                }
            } while (!cursor.equals("0"));
        } catch (Exception e) {
            throw new RuntimeException("Error during Redis key deletion: " + e.getMessage(), e);
        }
    }
}
