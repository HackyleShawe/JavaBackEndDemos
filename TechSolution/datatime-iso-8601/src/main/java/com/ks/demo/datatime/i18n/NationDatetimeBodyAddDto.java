package com.ks.demo.datatime.i18n;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;
import java.time.OffsetDateTime;

@Data
public class NationDatetimeBodyAddDto {
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private OffsetDateTime odt;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant instant;
}
