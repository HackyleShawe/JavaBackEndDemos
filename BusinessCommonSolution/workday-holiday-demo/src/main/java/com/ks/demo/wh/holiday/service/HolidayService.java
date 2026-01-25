package com.ks.demo.wh.holiday.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ks.demo.wh.common.constant.RegionEnum;
import com.ks.demo.wh.holiday.dto.HolidayCacheDto;
import com.ks.demo.wh.holiday.entity.SysHoliday;
import com.ks.demo.wh.util.BeanCopyUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Map;

@Slf4j
@Service
public class HolidayService {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Autowired
    private HolidayCacheService holidayCacheService;
    @Autowired
    private HolidayAdminService holidayAdminService;


    public boolean isHoliday() {
        return isHoliday(LocalDate.now());
    }

    /**
     * 查库没有，一定不是节假日？是的，现阶段设计只能如此
     * 查往年的日期，没找到，怎么判定？
     * 查明年的日期，没找到，怎么判定？
     * 再按照年份去查库，还是没有则抛出异常：节假日未初始化，请先初始化xx年的节假日信息
     */
    public boolean isHoliday(LocalDate localDate) {
        if(localDate == null) {
            return false;
        }

        HolidayCacheDto holidayCacheDto = holidayCacheService.get(localDate.format(FORMATTER));
        if(holidayCacheDto != null) {
            return true;
        }

        //如果时间在三年内，缓存中没有，则确定不是节假日？不行，要是缓存失效、不可用，则直接误判了！！！

        SysHoliday sysHoliday = holidayAdminService.getOne(Wrappers.<SysHoliday>lambdaQuery()
                .eq(SysHoliday::getHoliday, localDate)
                .eq(SysHoliday::getRegion, RegionEnum.CN.getCode())
                .eq(SysHoliday::getDeleted, Boolean.FALSE));
        if(sysHoliday != null) {
            HolidayCacheDto holidayCache = BeanCopyUtils.copy(sysHoliday, HolidayCacheDto.class);
            holidayCache.setHoliday(sysHoliday.getHoliday().format(FORMATTER));
            holidayCacheService.put(Collections.singletonList(holidayCache));
        } else {
            //再按照年份去查库，还是没有则抛出异常：节假日未初始化，请先初始化xx年的节假日信息
            //存在问题：如果缓存不命中，则没检查一个非节假日日期，都需要进行年份检查
            long count = holidayAdminService.count(Wrappers.<SysHoliday>lambdaQuery().eq(SysHoliday::getYear, localDate.getYear()));
            if(count < 1) {
                throw new RuntimeException("节假日未初始化，请先初始化"+localDate.getYear()+"年的节假日信息");
            }
        }

        return sysHoliday != null;
    }

    /**
     * 计算下一个节假日的日期
     */
    public LocalDate nextHoliday() {
        return nextHoliday(LocalDate.now(), 1);
    }
    public LocalDate nextHoliday(LocalDate localDate) {
        return nextHoliday(localDate, 1);
    }
    public LocalDate nextHoliday(LocalDate localDate, int days) {
        if(localDate == null || days < 1 || days > 366) {
            throw new IllegalArgumentException("nextHoliday: localDate is null or days is invalid");
        }

        //实现思路：日期不断+1，判断该个日期是否为节假日
        while (days > 0) {
            localDate = localDate.plusDays(1);
            if(isHoliday(localDate)) {
                days--;
            }
        }

        return localDate;
    }

    /**
     * 计算上一个节假日的日期
     */
    public LocalDate previousHoliday() {
        return previousHoliday(LocalDate.now(), 1);
    }
    public LocalDate previousHoliday(LocalDate localDate) {
        return previousHoliday(localDate, 1);
    }
    public LocalDate previousHoliday(LocalDate localDate, int days) {
        if(localDate == null || days < 1 || days > 366) {
            throw new IllegalArgumentException("nextHoliday: localDate is null or days is invalid");
        }

        //实现思路：日期不断-1，判断该个日期是否为节假日
        while (days > 0) {
            localDate = localDate.plusDays(-1);
            if(isHoliday(localDate)) {
                days--;
            }
        }

        return localDate;
    }

    /**
     * 计算休息日的天数
     */
    public int countHolidays() {
        Map<String, HolidayCacheDto> cacheMap = holidayCacheService.get();
        if(cacheMap != null && !cacheMap.isEmpty()) {
            return cacheMap.size();
        }

        long count = holidayAdminService.count(Wrappers.<SysHoliday>lambdaQuery()
                        .eq(SysHoliday::getYear, LocalDate.now().getYear())
                        .eq(SysHoliday::getRegion, RegionEnum.CN.getCode())
                        .eq(SysHoliday::getDeleted, Boolean.FALSE)
                        .groupBy(SysHoliday::getHoliday) );
        return (int) count;
    }
    public int countHolidays(LocalDate startDate, LocalDate endDate) {
        if(startDate == null || endDate == null) {
            throw new IllegalArgumentException("countHolidays: startDate or endDate is null");
        }
        if(startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("countHolidays: startDate is after endDate");
        }
        //if(startDate.getYear() != endDate.getYear()) {
        //    throw new IllegalArgumentException("countHolidays: startDate and endDate must be same year");
        //}

        LocalDate cur = startDate;
        int count = 0;
        //从开始日期到结束日期依次遍历
        do {
            if(isHoliday(cur)) {
                count++;
            }
            cur = cur.plusDays(1);
        } while (cur.isBefore(endDate));

        return count;
    }


    public boolean isWorkday() {
        return isWorkday(LocalDate.now());
    }
    public boolean isWorkday(LocalDate localDate) {
        boolean holiday = isHoliday(localDate);
        return !holiday;
    }

    /**
     * 计算下一个工作日的日期
     */
    public LocalDate nextWorkday() {
        return nextWorkday(LocalDate.now());
    }
    public LocalDate nextWorkday(LocalDate localDate) {
        return nextWorkday(localDate, 1);
    }
    public LocalDate nextWorkday(LocalDate localDate, int days) {
        if(localDate == null || days < 1 || days > 366) {
            throw new IllegalArgumentException("nextHoliday: localDate is null or days is invalid");
        }

        //实现思路：日期不断+1，判断该个日期是否为工作日
        while (days > 0) {
            localDate = localDate.plusDays(1);
            if(isWorkday(localDate)) {
                days--;
            }
        }

        return localDate;
    }

    /**
     * 计算上一个工作日的日期
     */
    public LocalDate previousWorkday() {
        return previousWorkday(LocalDate.now());
    }
    public LocalDate previousWorkday(LocalDate localDate) {
        return previousWorkday(localDate, 1);
    }
    public LocalDate previousWorkday(LocalDate localDate, int days) {
        if(localDate == null || days < 1 || days > 366) {
            throw new IllegalArgumentException("nextHoliday: localDate is null or days is invalid");
        }

        //实现思路：日期不断-1，判断该个日期是否为工作日
        while (days > 0) {
            localDate = localDate.plusDays(-1);
            if(isWorkday(localDate)) {
                days--;
            }
        }

        return localDate;
    }

    /**
     * 计算工作日的天数
     */
    public int countWorkdays() {
        LocalDate startDate = LocalDate.of(LocalDate.now().getYear(), 1, 1);
        LocalDate endDate = LocalDate.of(LocalDate.now().getYear(), 12, 31);
        long days = ChronoUnit.DAYS.between(startDate, endDate) + 1; //一年的总天数

        int holidays = countHolidays();
        return (int)days - holidays;
    }
    public int countWorkdays(LocalDate startDate, LocalDate endDate) {
        if(startDate == null || endDate == null) {
            throw new IllegalArgumentException("countHolidays: startDate or endDate is null");
        }
        if(startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("countHolidays: startDate is after endDate");
        }
        if(startDate.getYear() != endDate.getYear()) {
            throw new IllegalArgumentException("countHolidays: startDate and endDate must be same year");
        }

        LocalDate cur = startDate;
        int count = 0;
        //从开始日期到结束日期依次遍历
        do {
            cur = cur.plusDays(1);
            if(isWorkday(cur)) {
                count++;
            }
        } while (cur.isBefore(endDate));

        return count;
    }

}
