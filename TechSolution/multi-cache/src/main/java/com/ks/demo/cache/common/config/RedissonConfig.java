package com.ks.demo.cache.common.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {
    @Bean
    public RedissonClient redisson() {
        // 默认连接地址 127.0.0.1:6379
        RedissonClient redisson = Redisson.create();

        return redisson;
    }
}
