package com.ks.demo.wh.holiday.entity;


import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;

/**
 * 休息日信息表
 * @TableName sys_holiday
 */
@Data
public class SysHoliday implements Serializable {

    private Long id;
    /**
     * 年份，便于快速获取某年的所有节假日
     */
    private Integer year;
    /**
     * 节假日期
     */
    private LocalDate holiday;
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
