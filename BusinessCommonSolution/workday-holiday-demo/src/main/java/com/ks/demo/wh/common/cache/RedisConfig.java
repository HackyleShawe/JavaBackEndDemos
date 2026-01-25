package com.ks.demo.wh.common.cache;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * redis配置
 */
@Configuration
public class RedisConfig {
    @Autowired
    private RedisConnectionFactory factory;

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        //使用Jackson2JsonRedisSerializer来序列化和反序列化redis的value值（默认使用JDK的序列化方式）
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper om = new ObjectMapper();
        // 指定要序列化的域，field,get和set,以及修饰符范围，ANY是都有包括private和public
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // 指定序列化输入的类型，类必须是非final修饰的，final修饰的类，比如String,Integer等会跑出异常
        //om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        om.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);

        RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer()); //指定Redis的Key序列化方式
        template.setValueSerializer(jackson2JsonRedisSerializer); //指定Value的序列化方式
        template.setHashValueSerializer(jackson2JsonRedisSerializer); //指定Hash的Value的序列化方式
        template.setDefaultSerializer(new StringRedisSerializer());

        //ObjectMapper objectMapper = new ObjectMapper();
        //objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        //objectMapper.registerModule(new JavaTimeModule()); //关键：注册 Java 8 时间模块
        //objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); //避免时间变成时间戳
        ////GenericJackson2JsonRedisSerializer支持hash value，Jackson2JsonRedisSerializer是不支持的
        ////GenericJackson2JsonRedisSerializer反序列化时，Map / Object 一律还原成 LinkedHashMap
        //GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);
        //template.setHashKeySerializer(jackson2JsonRedisSerializer); //执行Hash的Key的序列化方式
        //template.setHashValueSerializer(serializer); //指定Hash的Value的序列化方式

        template.afterPropertiesSet();
        return template;
    }

}
