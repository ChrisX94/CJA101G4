package com.shakemate.shshop.util;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class ShShopRedisUtil {
    @Autowired
    private StringRedisTemplate redisTemplate;



    // set key-value
    public void set(String key, String value){
        redisTemplate.opsForValue().set(key, value);
    }

    // set key-value and timeoutSeconds
    public void set(String key, String value, long timeoutSeconds) {
        redisTemplate.opsForValue().set(key, value, Duration.ofSeconds(timeoutSeconds));
    }

    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }
    // 刪除 key
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    // 判斷 key 是否存在
    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
}
