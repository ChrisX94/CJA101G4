package com.shakemate.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis 設定類
 * 用來自定義 RedisTemplate，解決 Redis 存取物件時的序列化問題。
 * 預設支援 Key 為 String，Value 為 JSON 格式（通用物件）。
 */
@Configuration
public class RedisConfig {

    /**
     * 自定義 RedisTemplate
     * 支援 String 類型的 Key，以及通過 JSON 序列化的物件 Value。
     *
     * @param connectionFactory Redis 連線工廠（Spring Boot 自動注入）
     * @return RedisTemplate 實例
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        // 建立 RedisTemplate 實例
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // 建立 key 的序列化方式：使用 String，方便在 Redis CLI 直接閱讀
        StringRedisSerializer stringSerializer = new StringRedisSerializer();

        // 建立 value 的序列化方式：使用 JSON，通用且跨語言易讀
        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer();

        // key 和 hash key 都使用 String 序列化
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);

        // value 和 hash value 都使用 JSON 序列化
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);

        // 返回配置好的 template
        return template;
    }
}
