package com.ks.demo.wh.holiday.dto;


import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.io.Serializable;
import java.time.LocalDate;

@Data
public class HolidayQueryDto implements Serializable {

    private Long id;
    /**
     * 查询某个年份的节假日
     */
    private Integer year;

    /**
     * 查询某个月的节假日
     */
    @Min(value = 1, message = "月份不能小于1")
    @Max(value = 12, message = "月份不能大于12")
    private Integer month;

    /**
     * 节假日期
     */
    private LocalDate holiday;

}
