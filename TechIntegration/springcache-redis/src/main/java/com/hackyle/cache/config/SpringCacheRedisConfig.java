package com.hackyle.cache.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.lang.reflect.Method;
import java.time.Duration;

@Configuration
@EnableCaching // 开始springCache
public class SpringCacheRedisConfig extends CachingConfigurerSupport {
    private static final Logger LOGGER = LoggerFactory.getLogger(SpringCacheRedisConfig.class);

    /**
     * 缓存管理器：指定使用那种缓存的具体实现，方式一
     *
     * 只有CacheManger才能扫描到cacheable注解
     * Value使用Jackson工具序列化
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheManager cacheManager = RedisCacheManager.RedisCacheManagerBuilder
                .fromConnectionFactory(connectionFactory) //Redis链接工厂
                //缓存配置 通用配置  默认存储一小时
                .cacheDefaults(getCacheConfigurationWithTtl(Duration.ofHours(1)))
                //配置同步修改或删除  put/evict
                .transactionAware()
                //对于不同的cacheName我们可以设置不同的过期时间
                //.withCacheConfiguration("app:",getCacheConfigurationWithTtl(Duration.ofHours(5)))
                //.withCacheConfiguration("user:",getCacheConfigurationWithTtl(Duration.ofHours(2)))
                .build();
        return cacheManager;
    }
    private RedisCacheConfiguration getCacheConfigurationWithTtl(Duration duration) {
        return RedisCacheConfiguration
                .defaultCacheConfig()
                //设置key value的序列化方式
                // 设置key为String
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                // 设置value 为自动转Json的Object
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()))
                // 不缓存null
                .disableCachingNullValues()
                // 设置缓存的过期时间
                .entryTtl(duration);
    }


    /**
     * 缓存管理器：指定使用那种缓存的具体实现，方式二
     *
     * Value使用FastJSON序列化，需导入fastjson依赖包
     */
    //@Bean
    //public RedisCacheConfiguration redisCacheConfiguration() {
    //    FastJsonRedisSerializer<Object> fastJsonRedisSerializer = new FastJsonRedisSerializer<>(Object.class);
    //    RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();
    //    config = config
    //            .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(fastJsonRedisSerializer))
    //            .entryTtl(Duration.ofHours(2)); //默认有效时间为2h
    //    return config;
    //}


    /**
     * 自定义缓存Key的生成策略。
     * 当在Cacheable、CachePut、CacheEvict注解中没有指定key时生效
     * 默认的缓存Key为：类名+方法名+参数
     */
    @Bean
    @Override
    public KeyGenerator keyGenerator() {
        return new KeyGenerator() {
            @Override
            public Object generate(Object target, Method method, Object... params) {
                StringBuilder sb = new StringBuilder();
                sb.append(target.getClass().getName());
                sb.append(method.getName());
                for (Object obj : params
                ) {
                    sb.append(obj.toString());
                }
                return sb.toString();
            }
        };
    }

    /**
     * 缓存的异常处理
     */
    @Bean
    @Override
    public CacheErrorHandler errorHandler() {
        // 异常处理，当Redis发生异常时，打印日志，但是程序正常走
        LOGGER.info("初始化 -> [{}]", "SpringCacheErrorHandler");
        return new CacheErrorHandler() {
            @Override
            public void handleCacheGetError(RuntimeException e, Cache cache, Object key) {
                LOGGER.error("Redis occur handleCacheGetError：key -> [{}]", key, e);
            }
            @Override
            public void handleCachePutError(RuntimeException e, Cache cache, Object key, Object value) {
                LOGGER.error("Redis occur handleCachePutError：key -> [{}]；value -> [{}]", key, value, e);
            }
            @Override
            public void handleCacheEvictError(RuntimeException e, Cache cache, Object key) {
                LOGGER.error("Redis occur handleCacheEvictError：key -> [{}]", key, e);
            }
            @Override
            public void handleCacheClearError(RuntimeException e, Cache cache) {
                LOGGER.error("Redis occur handleCacheClearError：", e);
            }
        };
    }
}
