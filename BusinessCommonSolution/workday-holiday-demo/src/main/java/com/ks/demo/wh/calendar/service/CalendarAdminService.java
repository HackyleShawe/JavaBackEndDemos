package com.ks.demo.wh.calendar.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ks.demo.wh.calendar.entity.SysCalendar;
import com.ks.demo.wh.calendar.mapper.SysCalendarMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 日历（工作日、节假日）管理服务接口
 */
@Slf4j
@Service
public class CalendarAdminService extends ServiceImpl<SysCalendarMapper, SysCalendar> {

}
