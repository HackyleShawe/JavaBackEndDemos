package com.ks.demo.wh.calendar.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.ks.demo.wh.calendar.dto.CalendarCacheDto;
import com.ks.demo.wh.calendar.entity.SysCalendar;
import com.ks.demo.wh.calendar.mapper.SysCalendarMapper;
import com.ks.demo.wh.common.constant.RegionEnum;
import com.ks.demo.wh.common.helper.RedissonLockHelper;
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
 * 日历缓存
 * 两级缓存：
 * Level 1：JVM堆内缓存
 * Level 2：外部Redis缓存
 *
 * 注意：不要使用LocalDate作为Key：
 * - Redis序列化时如果需要支持LocalDate，需要额外配置，会影响其他场景下的序列化和反序列化
 * - Invocation of init method failed; nested exception is java.lang.ClassCastException:
 *      class java.time.LocalDate cannot be cast to class java.lang.String
 *   (java.time.LocalDate and java.lang.String are in module java.base of loader 'bootstrap')
 */
@Slf4j
@Service
public class CalendarCacheService {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Level 1：JVM堆内缓存，使用Guava作为JVM堆内缓存。不要使用ConcurrentHashMap，因为它不具备自动管理的能力
     * 不需要定义为static，因为Bean是单例
     * 缓存设计：Key: yyyy-MM-dd (String)；Val: CalenderCacheDto (JSON)
     */
    private final Cache<String, CalendarCacheDto> calendarCache = CacheBuilder.newBuilder()
            .initialCapacity(1200)                           // 设置初始容量
            //.concurrencyLevel(4)                          // 设置并发级别
            .maximumSize(1500)                               // 设置最大容量
            .expireAfterWrite(Duration.ofHours(1))    // 写入后过期时间
            .build(new CacheLoader<String, CalendarCacheDto>() {   // 设置缓存重新加载逻辑
                /**
                 * Guava不允许缓存value为null的key
                 * 因为：如果 load() 返回 null，Guava无法区分：key不存在、还是value就是 null
                 * 解决：使用一个类标识null
                 */
                @Override
                public CalendarCacheDto load(String key) {  // 设置缓存重新加载逻辑
                    // 重新加载指定Key的值
                    CalendarCacheDto cacheDto = loadOne(key);
                    return cacheDto == null ? NULL_VAL : cacheDto;
                }

                @Override
                public Map<String, CalendarCacheDto> loadAll(Iterable<? extends String> keys) throws Exception {
                    Map<String, CalendarCacheDto> calendarResMap = new HashMap<>();
                    Map<String, CalendarCacheDto> calendarMap = loadByYear();
                    for (String key : keys) {
                        CalendarCacheDto cacheDto = calendarMap.get(key);
                        calendarResMap.put(key, cacheDto == null ? NULL_VAL : cacheDto);
                    }
                    return calendarResMap;
                }
            });
    //标识值为NULL时的对象
    private static final CalendarCacheDto NULL_VAL = new CalendarCacheDto();

    /**
     * Level 2：外部Redis缓存
     * 数据结构：hash
     * Key:sys:calendar:年份
     * HKey: yyyy-MM-dd (String)
     * HVal: CalenderCacheDto (JSON)
     * 为什么用Hash，不用单个key？
     * - 数据一旦初始化，几乎不会更改，所以不存在每个hkey需要单独管理TTL的情况
     * - 如果拆分为单个key，key的数量太多了
     */
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private SysCalendarMapper sysCalendarMapper;
    @Autowired
    private RedissonLockHelper redissonLockHelper;

    private String getRedisKey() {
        return "sys:calendar:" + LocalDate.now().getYear();
    }

    /**
     * 初始化休息日信息到缓存
     * 年份交界处，怎么删除旧的和初始化新的？
     * - 旧的过期自动删除，新的根据key自动查库放入缓存
     * - 会有短暂这种情况：今年的key中，含有明年的的节假日。但随着过期自动删除，到达明年自动恢复
     */
    @PostConstruct
    public void initCache() {
        String lockKey = "sys:calendar:lock:init";
        //使用分布式锁，保证集群环境，同一时刻只有一个服务能进行初始化
        redissonLockHelper.executeWithLock(lockKey, 500L, Void -> {
            Map<String, CalendarCacheDto> holidayMap = loadByYear();
            // 1. 先写 Redis（保证对外一致性）
            redisTemplate.opsForHash().putAll(getRedisKey(), holidayMap);

            // 2. 设置过期时间
            redisTemplate.expire(getRedisKey(), 12, TimeUnit.HOURS);

            // 3. 再写本地缓存
            calendarCache.invalidateAll();
            calendarCache.putAll(holidayMap);
        });
    }
    private Map<String, CalendarCacheDto> loadByYear() {
        int year = LocalDate.now().getYear();
        List<SysCalendar> holidayList = sysCalendarMapper.selectList(Wrappers.<SysCalendar>lambdaQuery()
                //加载进三年的数据到缓存
                .in(SysCalendar::getYear, Arrays.asList(year-1, year, year+1))
                .eq(SysCalendar::getRegion, RegionEnum.CN.getCode())
                .eq(SysCalendar::getDeleted, Boolean.FALSE));
        if(CollectionUtils.isEmpty(holidayList)) {
            return Collections.emptyMap();
        }

        Map<String, CalendarCacheDto> holidayResMap = new HashMap<>(holidayList.size());
        for (SysCalendar sysCalendar : holidayList) {
            CalendarCacheDto holidayCacheDto = BeanCopyUtils.copy(sysCalendar, CalendarCacheDto.class);
            holidayCacheDto.setCalendarDate(sysCalendar.getCalendarDate().format(FORMATTER));

            holidayResMap.put(holidayCacheDto.getCalendarDate(), holidayCacheDto);
        }
        return holidayResMap;
    }
    private CalendarCacheDto loadOne(String date) {
        SysCalendar sysCalendar = sysCalendarMapper.selectOne(Wrappers.<SysCalendar>lambdaQuery()
                .eq(SysCalendar::getCalendarDate, date)
                .eq(SysCalendar::getRegion, RegionEnum.CN.getCode())
                .eq(SysCalendar::getDeleted, Boolean.FALSE)
                .last("LIMIT 1"));
        return BeanCopyUtils.copy(sysCalendar, CalendarCacheDto.class);
    }


    /**
     * 先放Redis，再放本地缓存
     */
    public void put(List<CalendarCacheDto> calenderCacheDtoList) {
        if(calenderCacheDtoList == null || calenderCacheDtoList.isEmpty()) {
            return;
        }

        Map<String, CalendarCacheDto> holidayMap = new HashMap<>(calenderCacheDtoList.size());
        for (CalendarCacheDto calendar : calenderCacheDtoList) {
            holidayMap.put(calendar.getCalendarDate(), calendar);
        }

        redisTemplate.opsForHash().putAll(getRedisKey(), holidayMap);
        calendarCache.putAll(holidayMap);
    }

    /**
     * 清除全部缓存
     */
    public boolean clear() {
        try {
            calendarCache.cleanUp();
            redisTemplate.delete(getRedisKey());
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    /**
     * 失效、删除某个key
     */
    public boolean evict(String date) {
        if(StringUtils.isBlank(date)) {
            return false;
        }
        try {
            calendarCache.invalidate(date);
            redisTemplate.opsForHash().delete(getRedisKey(), date);
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    /**
     * 先从一级缓存中获取，没有再从二级缓存中获取
     */
    public Map<String, CalendarCacheDto> get() {
        ConcurrentMap<String, CalendarCacheDto> holidayCacheMap = calendarCache.asMap();
        if(!CollectionUtils.isEmpty(holidayCacheMap)) {
            return holidayCacheMap;
        }

        Map<String, CalendarCacheDto> result = new HashMap<>();
        //获取整个map
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(getRedisKey());
        entries.forEach((hkey, hval) -> result.put(hkey.toString(), (CalendarCacheDto) hval));
        //塞入一级缓存
        if(!CollectionUtils.isEmpty(result)) {
            calendarCache.putAll(result);
        }

        return result;
    }

    public CalendarCacheDto get(String localDate) {
        if(StringUtils.isBlank(localDate)) {
            return null;
        }

        CalendarCacheDto calenderCacheDto = calendarCache.getIfPresent(localDate);
        if(calenderCacheDto != null) {
            return calenderCacheDto == NULL_VAL ? null : calenderCacheDto;
        }

        //获取Map中的某个hkey的值
        Object object = redisTemplate.opsForHash().get(getRedisKey(), localDate);
        //塞入一级缓存
        if(object != null) {
            calendarCache.put(localDate, JSON.parseObject(JSON.toJSONString(object), CalendarCacheDto.class));
        }

        return object == null ? null : JSON.parseObject(JSON.toJSONString(object), CalendarCacheDto.class);
    }

}
