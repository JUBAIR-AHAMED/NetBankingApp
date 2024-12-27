package com.netbanking.util;

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
    
    public static <T> void setex(String key, T map) throws JsonProcessingException {
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
                    System.out.println("Deleted key: " + key);
                }
            } while (!cursor.equals("0"));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedis.close();
        }
    }
}
