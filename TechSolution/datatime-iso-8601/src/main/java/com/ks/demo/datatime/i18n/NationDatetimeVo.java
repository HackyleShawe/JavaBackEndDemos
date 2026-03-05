package com.ks.demo.datatime.i18n;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.Instant;
import java.time.OffsetDateTime;

@Data
public class NationDatetimeVo {
    /**
     * 'T'	ISO-8601 分隔符
     * XXX	时区偏移量（+08:00）
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private OffsetDateTime odt;

    /**
     * Z 要加单引号
     * timezone 必须是 UTC
     */
    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'",
            timezone = "UTC" )
    private Instant instant;

}
