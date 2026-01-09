package com.hackyle.ssm.util;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.sql.Date;

@Component //将其交给Spring管理
public class StringToDateConverter implements Converter<String, Date> {

    @Override
    public Date convert(String s) {
        if("".equals(s)) {
            throw new RuntimeException("请传入数据");
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date date = new java.util.Date();
        try {
            date = simpleDateFormat.parse(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Date(date.getTime());
    }

}
