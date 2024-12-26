package com.netbanking.util;

import java.util.Map;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import redis.clients.jedis.Jedis;

public class Redis {
	static Jedis jedis;
	static final ObjectMapper objectMapper = new ObjectMapper();
	
    private Redis() {
    	if(jedis==null) {
    		jedis = new Jedis("localhost", 6379);
    	}
        //jedis.auth("yourpassword");
        //System.out.println("Connection Successful");
    }
    
    public static String get(String cacheKey) {
    	new Redis();
    	return jedis.get(cacheKey);
    }
    
    public static void setex(String key, String value) {
    	new Redis();
    	jedis.setex(key, 3600, value);
    }
    
    public static void setex(String key, Map<String, Object> map) throws JsonProcessingException {
    	String value = objectMapper.writeValueAsString(map);
    	setex(key, value);
    }
    
    public static boolean exists(String key) {
    	new Redis();
    	return jedis.exists(key);
    }
    
    public static int delete(String key) {
    	new Redis();
    	return (int) jedis.del(key);
    }
}
