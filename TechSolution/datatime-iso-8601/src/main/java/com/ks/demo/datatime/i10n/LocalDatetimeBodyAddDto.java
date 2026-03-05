package com.ks.demo.datatime.i10n;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class LocalDatetimeBodyAddDto {
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date dateTime;

    /**
     * 为什么要先@JsonFormat？
     * LocalDateTime 默认只支持 ISO-8601 格式（yyyy-MM-ddTHH:mm:ss）
     * 而我们定义的是（yyyy-MM-dd HH:mm:ss），跟ISO-8601的格式差了一个T，所以报错了解析失败
     * 在接收请求体中，我们先把此字段的时间格式化（所以@JsonFormat），再交给@DateTimeFormat解析
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime localDateTime;
}
