package com.ks.demo.wh.holiday.vo;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Data
public class HolidayVo implements Serializable {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;
    /**
     * 年份，便于快速获取某年的所有节假日
     */
    private Integer year;
    /**
     * 节假日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
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

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    private Long updateBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;

}
