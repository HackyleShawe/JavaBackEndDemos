package com.ks.demo.wh.calendar.entity;


import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 日历信息表
 * @TableName sys_calendar
 */
@Data
public class SysCalendar implements Serializable {

    private Long id;
    /**
     * 年份，便于快速获取某年的所有节假日
     */
    private Integer year;
    /**
     * 日期
     */
    private LocalDate calendarDate;
    /**
     * 0-False-非工作日, 1-True-是工作日
     */
    private Boolean workday;
    /**
     * 类型：HOLIDAY / WEEKEND / WORKDAY
     */
    private String type;
    /**
     * 节假日说明，例如：周六、周日、春节、国庆节
     */
    private String description;
    /**
     * 国家区域代码，例如CN、TW、HK
     */
    private String region;
    /**
     * 0-False-未删除, 1-True-已删除
     */
    private Boolean deleted;
    private Long createBy;
    private LocalDate createTime;
    private Long updateBy;
    private LocalDate updateTime;


}
