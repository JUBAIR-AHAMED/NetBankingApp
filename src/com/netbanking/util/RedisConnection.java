package com.netbanking.util;

import redis.clients.jedis.Jedis;

public class RedisConnection {
    public static Jedis main() {
        Jedis jedis = new Jedis("localhost", 6379);
//        jedis.auth("yourpassword"); // Optional if Redis is password-protected
        System.out.println("Connection Successful");
        System.out.println("Ping: " + jedis.ping());
        return jedis;
    }
}
