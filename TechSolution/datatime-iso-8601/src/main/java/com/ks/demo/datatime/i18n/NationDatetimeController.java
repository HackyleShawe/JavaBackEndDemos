package com.ks.demo.datatime.i18n;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ks.demo.datatime.infrastructure.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@RequestMapping("/i18n")
@RestController
public class NationDatetimeController {
    /**
     * 接收带偏移量的时间
     * DateTimeFormat必须指定iso = DateTimeFormat.ISO.DATE_TIME，表明接收的是ISO-8601格式的时间
     * 例如：2026-03-04T20:14:07+08:00
     * pattern表示不用ISO-8601格式的时间，而是自定义时间格式：yyyy-MM-dd HH:mm:ssXXX，XXX 表示时区偏移
     * 例如：2026-03-04 20:14:07+08:00
     * 注意：pattern 和 iso 不应该同时使用，如果指定了 pattern，iso 会被忽略。
     */
    @GetMapping("/parseOffsetDateTime")
    public ApiResponse<NationDatetimeVo> parseOffsetDateTime(@RequestParam("datetime")
                                                   @DateTimeFormat( //pattern = "yyyy-MM-dd HH:mm:ssXXX",
                                                           iso = DateTimeFormat.ISO.DATE_TIME)
                                                   OffsetDateTime date) {
        NationDatetimeVo vo = new NationDatetimeVo();
        vo.setOdt(date);
        vo.setInstant(date.toInstant());

        return ApiResponse.ok(vo);
    }

    /**
     * 接收UTC时间
     * DateTimeFormat必须指定iso = DateTimeFormat.ISO.DATE_TIME，表明接收的是ISO-8601格式的时间
     * 例如：2026-03-04T12:14:07Z
     * pattern表示不用ISO-8601格式的时间，而是自定义时间格式：yyyy-MM-dd HH:mm:ssZ，XXX 表示时区偏移
     * 例如：2026-03-04 12:14:07Z
     * 注意：pattern 和 iso 不应该同时使用，如果指定了 pattern，iso 会被忽略。
     */
    @GetMapping("/parseInstant")
    public ApiResponse<NationDatetimeVo> parseInstant(@RequestParam("datetime")
                                            @DateTimeFormat(//pattern = "yyyy-MM-dd HH:mm:ssZ", Instant 不建议配 pattern
                                                    iso = DateTimeFormat.ISO.DATE_TIME)
                                            Instant date) {
        NationDatetimeVo vo = new NationDatetimeVo();
        vo.setOdt(date.atOffset(ZoneOffset.of("+08:00")));
        vo.setInstant(date);
        return ApiResponse.ok(vo);
    }

    /**
     * 接收表单中带偏移量、UTC时间
     */
    @GetMapping("/parseByFrom")
    public ApiResponse<NationDatetimeVo> parseByFrom(NationDatetimeFormAddDto dto) {
        NationDatetimeVo vo = new NationDatetimeVo();
        vo.setOdt(dto.getOdt());
        vo.setInstant(dto.getInstant());

        return ApiResponse.ok(vo);
    }

    /**
     * 接收请求体中带偏移量、UTC时间
     */
    @PostMapping("/parseByBody")
    public ApiResponse<List<NationDatetimeVo>> parseByBody(@RequestBody NationDatetimeBodyAddDto dto) {

        if(dto.getInstant() != null) {
            DatetimeDemo demo = new DatetimeDemo();
            demo.setTimeStamp(dto.getInstant());
            demo.setEpochMilli(dto.getInstant().toEpochMilli());
            datetimeDemoMapper.insert(demo);
        }

        if(dto.getOdt() != null) {
            DatetimeDemo demo = new DatetimeDemo();
            demo.setTimeStamp(dto.getOdt().toInstant());
            demo.setEpochMilli(dto.getOdt().toInstant().toEpochMilli());
            datetimeDemoMapper.insert(demo);
        }

        List<NationDatetimeVo> datetimeVos = new ArrayList<>();
        List<DatetimeDemo> datetimeDemos = datetimeDemoMapper.selectList(Wrappers.<DatetimeDemo>lambdaQuery());
        for (DatetimeDemo datetimeDemo : datetimeDemos) {
            Instant timeStamp = datetimeDemo.getTimeStamp();
            Long epochMilli = datetimeDemo.getEpochMilli();

            NationDatetimeVo vo = new NationDatetimeVo();
            vo.setOdt(timeStamp.atOffset(ZoneOffset.of("+08:00")));
            vo.setInstant(timeStamp);
            datetimeVos.add(vo);

            vo = new NationDatetimeVo();
            vo.setOdt(Instant.ofEpochMilli(epochMilli).atOffset(ZoneOffset.of("+08:00")));
            vo.setInstant(Instant.ofEpochMilli(epochMilli));
            datetimeVos.add(vo);
        }

        return ApiResponse.ok(datetimeVos);
    }
    /*
    输入：
    {
      "odt" : "2026-03-05T20:00:00+08:00",
      "instant" : "2026-03-05T10:00:00Z"
    }
    输出：
    {
      "code": 200,
      "msg": "操作成功",
      "data": [
        {
          "odt": "2026-03-05T18:00:00+08:00",
          "instant": "2026-03-05T10:00:00Z"
        },
        {
          "odt": "2026-03-05T18:00:00+08:00",
          "instant": "2026-03-05T10:00:00Z"
        },
        {
          "odt": "2026-03-05T20:00:00+08:00",
          "instant": "2026-03-05T12:00:00Z"
        },
        {
          "odt": "2026-03-05T20:00:00+08:00",
          "instant": "2026-03-05T12:00:00Z"
        }
      ]
    }
     */

    @Autowired
    private DatetimeDemoMapper datetimeDemoMapper;

}
