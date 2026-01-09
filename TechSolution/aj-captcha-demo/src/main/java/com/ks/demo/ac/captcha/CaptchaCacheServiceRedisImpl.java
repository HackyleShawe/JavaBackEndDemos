package com.ks.demo.ac.captcha;

import com.anji.captcha.service.CaptchaCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * 自定义缓存的实现：CaptchaCacheService。
 * 参考：https://github.com/anji-plus/captcha/blob/master/service/springboot/src/main/java/com/anji/captcha/demo/service/CaptchaCacheServiceRedisImpl.java
 * 使生效：
 * 1.在resources目录下创建目录META-INF/service
 * 2.再创建文件：com.anji.captcha.service.CaptchaCacheService，指定自定义缓存的实现类：com.ks.demo.ac.captcha.CaptchaCacheServiceRedisImpl
 */
public class CaptchaCacheServiceRedisImpl implements CaptchaCacheService {
    /**
     * application.properties中的"aj.captcha.cache-type"值为"redis"
     */
    @Override
    public String type() {
        return "redis";
    }

    /**
     * 本质是RedisTemplate
     */
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void set(String key, String value, long expiresInSeconds) {
        stringRedisTemplate.opsForValue().set(key, value, expiresInSeconds, TimeUnit.SECONDS);
    }

    @Override
    public boolean exists(String key) {
        return stringRedisTemplate.hasKey(key);
    }

    @Override
    public void delete(String key) {
        stringRedisTemplate.delete(key);
    }

    @Override
    public String get(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    @Override
    public Long increment(String key, long val) {
        return stringRedisTemplate.opsForValue().increment(key,val);
    }
}
