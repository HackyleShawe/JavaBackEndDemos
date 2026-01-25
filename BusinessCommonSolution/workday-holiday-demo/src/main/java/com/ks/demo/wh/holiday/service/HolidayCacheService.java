package com.ks.demo.wh.holiday.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.ks.demo.wh.common.constant.RegionEnum;
import com.ks.demo.wh.holiday.dto.HolidayCacheDto;
import com.ks.demo.wh.holiday.entity.SysHoliday;
import com.ks.demo.wh.holiday.mapper.SysHolidayMapper;
import com.ks.demo.wh.util.BeanCopyUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * 节假日缓存
 * 两级缓存：
 * Level 1：JVM堆内缓存
 * Level 2：外部Redis缓存
 *
 * 设计要领：
 * TTL：L1 一定要比 L2 短。L1一小时，L2半天。
 * 缓存更新策略（写）：Cache Aside，先更新DB，再删L2，再删L1
 */
@Slf4j
@Service
public class HolidayCacheService {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Level 1：JVM堆内缓存，使用Guava作为JVM堆内缓存。不要使用ConcurrentHashMap，因为它不具备自动管理的能力
     * 不需要定义为static，因为Bean是单例
     * Key: yyyy-MM-dd (String)
     * Val: HolidayCacheDto (JSON)
     */
    private final Cache<String, HolidayCacheDto> holidayCache = CacheBuilder.newBuilder()
            .initialCapacity(120)                           // 设置初始容量，一年的节假日大概在120左右
            //.concurrencyLevel(4)                          // 设置并发级别
            .maximumSize(1000)                               // 设置最大容量
            .expireAfterWrite(Duration.ofHours(1))    // 写入后过期时间
            .build(new CacheLoader<String, HolidayCacheDto>() {   // 设置缓存重新加载逻辑
                /**
                 * Guava不允许缓存value为null的key
                 * 因为：如果 load() 返回 null，Guava无法区分：key不存在、还是value就是 null
                 * 解决：使用一个类标识null
                 */
                @Override
                public HolidayCacheDto load(String key) {  // 设置缓存重新加载逻辑
                    // 重新加载指定Key的值
                    HolidayCacheDto cacheDto = loadOne(key);
                    return cacheDto == null ? NULL_VAL : cacheDto;
                }

                @Override
                public Map<String, HolidayCacheDto> loadAll(Iterable<? extends String> keys) throws Exception {
                    Map<String, HolidayCacheDto> holidayResMap = new HashMap<>();
                    Map<String, HolidayCacheDto> holidayMap = loadByYear();
                    for (String key : keys) {
                        HolidayCacheDto cacheDto = holidayMap.get(key);
                        holidayResMap.put(key, cacheDto == null ? NULL_VAL : cacheDto);
                    }
                    return holidayResMap;
                }
            });
    //标识值为NULL时的对象
    private static final HolidayCacheDto NULL_VAL = new HolidayCacheDto();

    /**
     * Level 2：外部Redis缓存
     * 数据结构：hash
     * Key:sys:holiday:年份
     * HKey: yyyy-MM-dd (String)
     * HVal: HolidayCacheDto (JSON)
     */
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String REDIS_KEY = "sys:holiday:" + LocalDate.now().getYear();

    @Autowired
    private SysHolidayMapper sysHolidayMapper;

    /**
     * 初始化休息日信息到缓存
     * 年份交界处，怎么删除旧的和初始化新的？
     * - 旧的过期自动删除，新的根据key自动查库放入缓存
     * - 会有短暂这种情况：今年的key中，含有明年的的节假日。但随着过期自动删除，到达明年自动恢复
     */
    @PostConstruct
    public void initCache() {
        Map<String, HolidayCacheDto> holidayMap = loadByYear();
        holidayCache.putAll(holidayMap);

        redisTemplate.delete(REDIS_KEY); //初始化时先删除既有缓存
        redisTemplate.opsForHash().putAll(REDIS_KEY, holidayMap);
        Boolean hasKey = redisTemplate.hasKey(REDIS_KEY);//检查是否存在hash这个key
        if(hasKey) {
            redisTemplate.expire(REDIS_KEY, 12, TimeUnit.HOURS); //设置有效期
        }
    }
    private Map<String, HolidayCacheDto> loadByYear() {
        int year = LocalDate.now().getYear();
        List<SysHoliday> holidayList = sysHolidayMapper.selectList(Wrappers.<SysHoliday>lambdaQuery()
                //加载进三年的数据到缓存
                .in(SysHoliday::getYear, Arrays.asList(year-1, year, year+1))
                .eq(SysHoliday::getRegion, RegionEnum.CN.getCode())
                .eq(SysHoliday::getDeleted, Boolean.FALSE));
        if(CollectionUtils.isEmpty(holidayList)) {
            return Collections.emptyMap();
        }

        Map<String, HolidayCacheDto> holidayResMap = new HashMap<>(holidayList.size());
        for (SysHoliday sysHoliday : holidayList) {
            HolidayCacheDto holidayCacheDto = BeanCopyUtils.copy(sysHoliday, HolidayCacheDto.class);
            holidayCacheDto.setHoliday(sysHoliday.getHoliday().format(FORMATTER));

            holidayResMap.put(holidayCacheDto.getHoliday(), holidayCacheDto);
        }
        return holidayResMap;
    }
    private HolidayCacheDto loadOne(String date) {
        SysHoliday sysHoliday = sysHolidayMapper.selectOne(Wrappers.<SysHoliday>lambdaQuery()
                .eq(SysHoliday::getHoliday, date)
                .eq(SysHoliday::getRegion, RegionEnum.CN.getCode())
                .eq(SysHoliday::getDeleted, Boolean.FALSE)
                .last("LIMIT 1"));
        return BeanCopyUtils.copy(sysHoliday, HolidayCacheDto.class);
    }


    /**
     * 先放入一级缓存，再放入二级缓存，如果两者都放入失败，则返回失败
     */
    public boolean put(List<HolidayCacheDto> calenderCacheDtoList) {
        if(calenderCacheDtoList == null || calenderCacheDtoList.isEmpty()) {
            return false;
        }
        boolean level1Result = true;
        boolean level2Result = true;

        Map<String, HolidayCacheDto> holidayMap = new HashMap<>(calenderCacheDtoList.size());
        for (HolidayCacheDto holiday : calenderCacheDtoList) {
            holidayMap.put(holiday.getHoliday(), holiday);
        }
        try {
            holidayCache.putAll(holidayMap);
        } catch (Exception e) {  //捕获局部异常，防止放入Level1失败导致放入Level2失败
            level1Result = false;
            log.error("缓存节假日信息到Guava失败：", e);
        }

        try {
            redisTemplate.opsForHash().putAll(REDIS_KEY, holidayMap);
        } catch (Exception e) {
            level2Result = false;
            log.error("缓存节假日信息到Redis失败：", e);
        }

        return level1Result || level2Result; //只要放入成功一个，就整体返回成功
    }

    public boolean del() {
        try {
            holidayCache.cleanUp();
            redisTemplate.delete(REDIS_KEY);
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public boolean del(String date) {
        if(StringUtils.isBlank(date)) {
            return false;
        }
        try {
            holidayCache.invalidate(date);
            redisTemplate.opsForHash().delete(REDIS_KEY, date);
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    /**
     * 先从一级缓存中获取，没有再从二级缓存中获取
     */
    public Map<String, HolidayCacheDto> get() {
        ConcurrentMap<String, HolidayCacheDto> holidayCacheMap = holidayCache.asMap();
        if(!CollectionUtils.isEmpty(holidayCacheMap)) {
            return holidayCacheMap;
        }

        Map<String, HolidayCacheDto> result = new HashMap<>();
        //获取整个map
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(REDIS_KEY);
        entries.forEach((hkey, hval) -> result.put(hkey.toString(), (HolidayCacheDto) hval));
        //塞入一级缓存
        if(!CollectionUtils.isEmpty(result)) {
            holidayCache.putAll(result);
        }

        return result;
    }

    public HolidayCacheDto get(String localDate) {
        if(StringUtils.isBlank(localDate)) {
            return null;
        }

        HolidayCacheDto calenderCacheDto = null;
        try {
            calenderCacheDto = holidayCache.getIfPresent(localDate);
        } catch (Exception e) {
            log.error("从Guava中获取{}数据异常：", localDate, e);
        }
        if(calenderCacheDto != null) {
            return calenderCacheDto;
        }

        //获取Map中的某个hkey的值
        Object object = redisTemplate.opsForHash().get(REDIS_KEY, localDate);

        //塞入一级缓存
        if(object != null) {
            holidayCache.put(localDate, JSON.parseObject(JSON.toJSONString(object), HolidayCacheDto.class));
        }

        return object == null ? null : JSON.parseObject(JSON.toJSONString(object), HolidayCacheDto.class);
    }

}

/*
 * 不要使用LocalDate作为Key：
 * - Redis序列化时如果需要支持LocalDate，需要额外配置，会影响其他场景下的序列化和反序列化
 * - Invocation of init method failed; nested exception is java.lang.ClassCastException:
 *      class java.time.LocalDate cannot be cast to class java.lang.String
 *   (java.time.LocalDate and java.lang.String are in module java.base of loader 'bootstrap')
 */
