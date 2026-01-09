package com.ks.redisdemo.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.Jedis;

@ConditionalOnClass(Jedis.class) //只有存在Jedis这个字节码文件才加载此类
@Configuration
@EnableConfigurationProperties(RedisProperties.class) //类似于@Import，将RedisProperties类实例载入
public class RedisAutoConfigure {
    /**
     * 提供Jedis的Bean
     */
    @Bean
    public Jedis jedis(RedisProperties redisProperties){
        //System.out.println("在项目启动的时候就实例化这个类");
        return new Jedis(redisProperties.getHost(), redisProperties.getPort());
    }
}
