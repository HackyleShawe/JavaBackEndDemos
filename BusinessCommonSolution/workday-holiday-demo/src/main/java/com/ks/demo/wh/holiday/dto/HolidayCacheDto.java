package com.ks.demo.wh.holiday.dto;


import lombok.Data;

import java.io.Serializable;


@Data
public class HolidayCacheDto implements Serializable {

    private Long id;
    /**
     * 年份，便于快速获取某年的所有节假日
     */
    private Integer year;
    /**
     * 节假日期，格式：yyyy-MM-dd
     */
    //@JsonFormat(pattern = "yyyy-MM-dd")
    //private LocalDate holiday; Redis序列化时如果需要支持LocalDate，需要额外配置，会影响其他场景下的序列化和反序列化
    private String holiday;
    /**
     * 节假日说明，例如：周六、周日、春节、国庆节
     */
    private String description;
    /**
     * 国家区域代码，例如CN、TW、HK
     */
    private String region;

}
