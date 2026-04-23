package com.ks.demo.cache.config;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.ks.demo.cache.common.helper.RedissonLockHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Service
public class ConfigCacheService implements MultiLevelCache<String, SysConfigEntity> {
    private static final String NULL_VAL = "NULL";

    /**
     * Level 1：JVM堆内缓存，Caffeine。不要使用ConcurrentHashMap，因为它不具备自动管理的能力
     * Key：configKey，Val：实体JSON
     * 为什么不用refreshAfterWrite？
     * - 它的含义是：一定时间后，下一次访问时触发异步刷新。
     * - 如果某个key很久没有被访问，则其不会被刷新；如果被高频访问，则会在短时间内快速刷新，请求打到Redis、DB，集群上可能形成周期性缓存风暴
     */
    private LoadingCache<String, String> caffeineCache = Caffeine.newBuilder()
            .maximumSize(1000)  // 设置最大缓存数量：根据系统的sys_config表的数量、内存容量评估
            //加上随机过期时间，防止热点Key同时失效，导致缓存雪崩
            .expireAfterWrite(300+ThreadLocalRandom.current().nextInt(1, 60), TimeUnit.SECONDS)
            .build(new CacheLoader<String, String>() {
                @Override
                public String load(String key) {
                    return loadConfig(key);
                }
            });

    /**
     * Level 2：外部Redis缓存
     * Key：PREFIX_configKey，Val：实体JSON
     * 为什么不用Hash，而用单个key？
     * - 数据一旦初始化，更改的可能性是很高的，
     * - hash中不能对单个hkey管理TTL，这导致没更改一个，都要整体更新整个hash的TTL，可能造成获取缓存时的抖动
     */
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    private String CONFIG_KEY = "sys:config:";

    @Autowired
    private RedissonLockHelper redissonLockHelper;

    @Autowired
    private SysConfigMapper sysConfigMapper;

    //@Override
    public SysConfigEntity getv1(String key) {
        //查L1本地缓存
        //利用Caffeine的这个特性：同一个 key 在加载过程中，其他线程会等待第一个线程完成，并复用结果。
        //将L2、L3级的获取封装在一起，可以避免和解决：热点击穿、并发查库
        //注意，这是单机的。如果是集群环境，每台机器都会查一次 Redis/DB，这叫做：跨节点缓存击穿，解决方案还是需要加分布式锁
        String configJson = caffeineCache.get(key, this::loadConfig);

        if (configJson == null || NULL_VAL.equals(configJson)) {
            return null;
        }
        return JSON.parseObject(configJson, SysConfigEntity.class);
    }
    private String loadConfig(String key) {
        String redisKey = CONFIG_KEY + key;
        //查L2Redis缓存
        Object object = redisTemplate.opsForValue().get(redisKey);
        if (object != null) {
            //caffeineCache.put(key, entity); 注意不需要这个了，Caffeine自动完成回填
            return (String) object;
        }

        //L3查数据
        //并发环境中保证只有一个线程可以去查库：分布式锁
        String configRes = redissonLockHelper.executeWithLock(CONFIG_KEY+key+":lock", 500, () -> {
            //拿到锁后再Double Check一下Redis中到底有没有
            Object objectCheck = redisTemplate.opsForValue().get(redisKey);
            if (objectCheck != null) {
                return (String) objectCheck;
            }

            SysConfigEntity config = sysConfigMapper.selectOne(Wrappers.<SysConfigEntity>lambdaQuery()
                    .eq(SysConfigEntity::getConfigKey, key)
                    .eq(SysConfigEntity::getStatus, Boolean.TRUE)
                    .eq(SysConfigEntity::getDeleted, Boolean.FALSE)
                    .last(  "limit 1"));
            //Caffeine默认不缓存null，也就是get方法默认不缓存null，所以需要在Redis层面做，解决缓存穿透
            String res = config == null ? NULL_VAL : JSON.toJSONString(config);
            long ttl = 24 + ThreadLocalRandom.current().nextInt(0, 5); //设置随机TTL，防止缓存雪崩
            redisTemplate.opsForValue().set(redisKey, res, ttl, TimeUnit.HOURS);
            //caffeineCache.put(key, JSON.toJSONString(config)); 注意不需要这个了，Caffeine自动完成回填

            return res;
        });

        return configRes;
    }

    @Override
    public SysConfigEntity get(String key) {
        //查L1本地缓存
        //为什么不用get，没有值自动加载？只想看缓存有没有，没有看下一级缓存即可，并不想让其构建缓存
        //get会在miss时执行查库加载逻辑，二级缓存就失去了意义，因为我总是要去查库了，为什么还要L2级缓存
        String configJson = caffeineCache.getIfPresent(key);
        if (configJson != null) {
            if (NULL_VAL.equals(configJson)) {
                return null;
            }
            return JSON.parseObject(configJson, SysConfigEntity.class);
        }

        //查L2Redis缓存
        String redisKey = CONFIG_KEY + key;
        Object object = redisTemplate.opsForValue().get(redisKey);
        if (object != null) {
            if(NULL_VAL.equals(object)) {
                return null;
            }

            caffeineCache.put(key, (String) object); //回填L1
            //return JSON.parseObject(JSON.toJSONString(object), SysConfigEntity.class); 导出原始JSON串外再套了一层JSON
            return JSON.parseObject((String) object, SysConfigEntity.class);
        }

        //L3查数据
        //并发环境中保证只有一个线程可以去查库：分布式锁
        String configRes = redissonLockHelper.executeWithLock(CONFIG_KEY+key+":lock", 500, () -> {
            //拿到锁后再Double Check一下Redis中到底有没有
            Object objectCheck = redisTemplate.opsForValue().get(redisKey);
            if (objectCheck != null) {
                return (String) objectCheck;
            }

            SysConfigEntity config = sysConfigMapper.selectOne(Wrappers.<SysConfigEntity>lambdaQuery()
                    .eq(SysConfigEntity::getConfigKey, key)
                    .eq(SysConfigEntity::getStatus, Boolean.TRUE)
                    .eq(SysConfigEntity::getDeleted, Boolean.FALSE)
                    .last(  "limit 1"));
            //Caffeine默认不缓存null，也就是get方法默认不缓存null，所以需要在Redis层面做，解决缓存穿透
            String res = config == null ? NULL_VAL : JSON.toJSONString(config);
            long ttl = 24 + ThreadLocalRandom.current().nextInt(0, 5); //设置随机TTL，防止缓存雪崩
            redisTemplate.opsForValue().set(redisKey, res, ttl, TimeUnit.HOURS);
            caffeineCache.put(key, res);

            return res;
        });

        return StringUtils.isBlank(configRes) || NULL_VAL.equals(configRes) ? null : JSON.parseObject(configRes, SysConfigEntity.class);
    }


     @Override
    public void put(String key, SysConfigEntity value) {
        if(StringUtils.isBlank(key)) {
            return;
        }
         String res = value == null ? NULL_VAL : JSON.toJSONString(value);
        long ttl = 24 + ThreadLocalRandom.current().nextInt(0, 5); //设置随机TTL，防止缓存雪崩
        redisTemplate.opsForValue().set(CONFIG_KEY + key, res, ttl, TimeUnit.HOURS);
        caffeineCache.put(key, res);
    }

    @Override
    public void evict(String key) {
        if(StringUtils.isBlank(key)) {
            return;
        }
        String redisKey = CONFIG_KEY + key;
        redisTemplate.delete(redisKey);
        caffeineCache.invalidate(key);
    }

    @Override
    public void clear() {
        caffeineCache.invalidateAll();
        Set<String> keys = redisTemplate.keys(CONFIG_KEY + "*");
        if (!keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }
}
