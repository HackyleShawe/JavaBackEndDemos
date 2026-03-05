package com.ks.demo.datatime.i10n;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class LocalDatetimeAddDto {
    private Date dateTime;

    private LocalDateTime localDateTime;
}
