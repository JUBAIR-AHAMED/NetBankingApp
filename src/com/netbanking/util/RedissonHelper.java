package com.netbanking.util;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

public class RedissonHelper {
	private static RedissonClient redisson;
	
	static {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");
        redisson = Redisson.create(config);
    }
	
	public static RedissonClient getInstance() {
		return redisson;
	}
}