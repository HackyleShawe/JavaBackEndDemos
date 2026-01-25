package com.ks.demo.wh.holiday.dto;


import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class HolidayUpdateDto implements Serializable {

    @NotNull
    private Long id;

    /**
     * 节假日说明，例如：周六、周日、春节、国庆节
     */
    private String description;
    /**
     * 国家区域代码，例如CN、TW、HK
     */
    private String region;

}
