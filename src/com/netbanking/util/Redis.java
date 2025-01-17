package com.netbanking.util;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.resps.ScanResult;

public class Redis {
	static Jedis jedis;
	static final ObjectMapper objectMapper = new ObjectMapper();
	
    private Redis() {
    	if(jedis==null) {
    		jedis = new Jedis("localhost", 6379);
    	}
    }
    
    public static String get(String cacheKey) {
    	new Redis();
    	return jedis.get(cacheKey);
    }
    
    public static void setex(String key, String value) {
    	new Redis();
    	jedis.setex(key, 3600, value);
    }
    
    public static <T> void setex(String key, T value) throws JsonProcessingException {
    	if(exists(key)) {
    		return;
    	}
    	String stringValue = objectMapper.writeValueAsString(value);
    	setex(key, stringValue);
    }
    
    public static void setList(String key, String id, List<Map<String, Object>> list) throws JsonProcessingException {
    	for(Map<String, Object> map:list) {
			Long idValue = (Long) map.get(id);
			String cacheKey = key+idValue;
			Redis.setex(cacheKey, map);
		}
    }
    
    public static boolean exists(String key) {
    	new Redis();
    	return jedis.exists(key);
    }
    
    public static int delete(String key) {
    	new Redis();
    	if(exists(key)) {
    		return (int) jedis.del(key);
    	}
    	return -1;
    }
    
    public static void deleteKeysWithStartString(String cacheKey) {
    	new Redis();
        String cursor = "0";
        try {
            do {
                ScanResult<String> result = jedis.scan(cursor, 
                		new redis.clients.jedis.params.ScanParams().match(cacheKey+"*").count(100));                
                cursor = result.getCursor();
                for (String key : result.getResult()) {
                    jedis.del(key);
                }
            } while (!cursor.equals("0"));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedis.close();
        }
    }
}
