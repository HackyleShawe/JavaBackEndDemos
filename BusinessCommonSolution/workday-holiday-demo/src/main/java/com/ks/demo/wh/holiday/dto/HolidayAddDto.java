package com.ks.demo.wh.holiday.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;

@Data
public class HolidayAddDto implements Serializable {

    /**
     * 年份，便于快速获取某年的所有节假日
     */
    @NotNull
    private Integer year;
    /**
     * 节假日期
     */
    @NotNull
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

}
