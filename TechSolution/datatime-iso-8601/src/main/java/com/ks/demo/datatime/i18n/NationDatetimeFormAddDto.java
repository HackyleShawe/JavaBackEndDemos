package com.ks.demo.datatime.i18n;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;
import java.time.OffsetDateTime;

@Data
public class NationDatetimeFormAddDto {
    /**
     * 接收带偏移量的时间
     * DateTimeFormat必须指定iso = DateTimeFormat.ISO.DATE_TIME，表明接收的是ISO-8601格式的时间
     * 例如：2026-03-04T20:14:07+08:00
     * pattern表示不用ISO-8601格式的时间，而是自定义时间格式：yyyy-MM-dd HH:mm:ssXXX，XXX 表示时区偏移
     * 例如：2026-03-04 20:14:07+08:00
     * 注意：pattern 和 iso 不应该同时使用，如果指定了 pattern，iso 会被忽略。
     */
    @DateTimeFormat( //pattern = "yyyy-MM-dd HH:mm:ssXXX",
            iso = DateTimeFormat.ISO.DATE_TIME)
    private OffsetDateTime odt;

    /**
     * 接收UTC时间
     * DateTimeFormat必须指定iso = DateTimeFormat.ISO.DATE_TIME，表明接收的是ISO-8601格式的时间
     * 例如：2026-03-04T12:14:07Z
     * pattern表示不用ISO-8601格式的时间，而是自定义时间格式：yyyy-MM-dd HH:mm:ssZ，XXX 表示时区偏移
     * 例如：2026-03-04 12:14:07Z
     * 注意：pattern 和 iso 不应该同时使用，如果指定了 pattern，iso 会被忽略。
     */
    @DateTimeFormat(//pattern = "yyyy-MM-dd HH:mm:ssZ", Instant 不建议配 pattern
            iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant instant;
}
