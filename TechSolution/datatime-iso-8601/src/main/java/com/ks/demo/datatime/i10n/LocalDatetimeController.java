package com.ks.demo.datatime.i10n;

import com.ks.demo.datatime.infrastructure.ApiResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@RequestMapping("/i10n")
@RestController
public class LocalDatetimeController {

    @GetMapping("/parseDate")
    public ApiResponse<String> parseDate(@RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        return ApiResponse.ok(date.toString());
    }
    @GetMapping("/parseLocalDate")
    public ApiResponse<String> parseLocalDate(@RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        return ApiResponse.ok(date.toString());
    }

    @GetMapping("/parseDateTime")
    public ApiResponse<String> parseDateTime(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date date) {
        return ApiResponse.ok(date.toString());
    }
    @GetMapping("/parseLocalDateTime")
    public ApiResponse<String> parseLocalDateTime(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime date) {
        return ApiResponse.ok(date.toString());
    }
    @GetMapping("/parseDateByFrom")
    public ApiResponse<LocalDatetimeVo> parseDateByFrom(LocalDatetimeFormAddDto dto) {
        LocalDatetimeVo vo = new LocalDatetimeVo();
        vo.setDateTime(dto.getDateTime());
        vo.setLocalDateTime(dto.getLocalDateTime());

        return ApiResponse.ok(vo);
    }

    @PostMapping("/parseDateByBody")
    public ApiResponse<LocalDatetimeVo> parseDateByBody(@RequestBody LocalDatetimeBodyAddDto dto) {
        LocalDatetimeVo vo = new LocalDatetimeVo();
        vo.setDateTime(dto.getDateTime());
        vo.setLocalDateTime(dto.getLocalDateTime());

        return ApiResponse.ok(vo);
    }


    /**
     * 不指定时间格式化注解@DateTimeFormat，默认使用YML文件中的配置
     */
    @GetMapping("/parseDateByYML")
    public ApiResponse<String> parseDateByYML(@RequestParam("date") LocalDate date) {
        return ApiResponse.ok(date.toString());
    }
    @GetMapping("/parseDateTimeByYML")
    public ApiResponse<String> parseDateTimeByYML(@RequestParam LocalDateTime date) {
        return ApiResponse.ok(date.toString());
    }
    @PostMapping("/parseDateTimeBodyByYML")
    public ApiResponse<LocalDatetimeVo> parseDateTimeBodyByYML(@RequestBody LocalDatetimeAddDto dto) {
        LocalDatetimeVo vo = new LocalDatetimeVo();
        vo.setDateTime(dto.getDateTime());
        vo.setLocalDateTime(dto.getLocalDateTime());

        return ApiResponse.ok(vo);
    }

}
