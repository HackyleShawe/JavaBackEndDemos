package com.ks.demo.wh.calendar.service;


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ks.demo.wh.calendar.dto.CalendarCacheDto;
import com.ks.demo.wh.calendar.entity.SysCalendar;
import com.ks.demo.wh.common.constant.RegionEnum;
import com.ks.demo.wh.util.BeanCopyUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class CalendarService {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Autowired
    private CalendarAdminService calendarAdminService;
    @Autowired
    private CalendarCacheService calendarCacheService;


    public boolean isHoliday() {
        return isHoliday(LocalDate.now());
    }
    public boolean isHoliday(LocalDate localDate) {
        if(localDate == null) {
            return false;
        }

        CalendarCacheDto cacheDto = calendarCacheService.get(localDate.format(FORMATTER));
        if(cacheDto != null) {
            return !cacheDto.getWorkday();
        }

        SysCalendar calendar = calendarAdminService.getOne(Wrappers.<SysCalendar>lambdaQuery()
                .eq(SysCalendar::getCalendarDate, localDate)
                .eq(SysCalendar::getRegion, RegionEnum.CN.getCode())
                .eq(SysCalendar::getDeleted, Boolean.FALSE)
                .last("limit 1"));
        if(calendar == null) {
            throw new RuntimeException("日历未初始化，请先初始化"+localDate.getYear()+"年的日历信息");
        }
        CalendarCacheDto calendarCacheDto = BeanCopyUtils.copy(calendar, CalendarCacheDto.class);
        calendarCacheDto.setCalendarDate(calendar.getCalendarDate().format(FORMATTER));
        calendarCacheService.put(Collections.singletonList(calendarCacheDto));

        return calendar.getWorkday();
    }

    /**
     * 计算下一个节假日的日期
     */
    public LocalDate nextHoliday() {
        return nextHoliday(LocalDate.now());
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
     * 计算上一个工作日的日期
     */
    public LocalDate previousHoliday() {
        return previousHoliday(LocalDate.now());
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
        int year = LocalDate.now().getYear();
        Map<String, CalendarCacheDto> cacheMap = calendarCacheService.get();
        if(cacheMap != null && !cacheMap.isEmpty()) {
            AtomicInteger holidays = new AtomicInteger();
            cacheMap.forEach((key, value) -> {
                if(year == value.getYear() && !value.getWorkday()) {
                    holidays.getAndIncrement();
                }
            });
            return holidays.get();
        }

        long count = calendarAdminService.count(Wrappers.<SysCalendar>lambdaQuery()
                .eq(SysCalendar::getYear, LocalDate.now().getYear())
                .eq(SysCalendar::getWorkday, Boolean.FALSE)
                .eq(SysCalendar::getRegion, RegionEnum.CN.getCode())
                .eq(SysCalendar::getDeleted, Boolean.FALSE)
                .groupBy(SysCalendar::getCalendarDate) );
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
        //if(startDate.getYear() != endDate.getYear()) {
        //    throw new IllegalArgumentException("countHolidays: startDate and endDate must be same year");
        //}

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
