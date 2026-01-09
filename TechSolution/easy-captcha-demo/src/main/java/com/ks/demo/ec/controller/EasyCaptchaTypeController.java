package com.ks.demo.ec.controller;

import com.alibaba.fastjson2.JSONObject;
import com.wf.captcha.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
public class EasyCaptchaTypeController {
    @Autowired
    private RedisTemplate redisTemplate;

    @RequestMapping("/captcha/png")
    public JSONObject png() {
        SpecCaptcha specCaptcha = new SpecCaptcha(130, 48, 5);
        String verCode = specCaptcha.text();
        String key = UUID.randomUUID().toString();

        // 存入redis并设置过期时间
        redisTemplate.opsForValue().set(key, verCode, 1, TimeUnit.MINUTES);

        JSONObject obj = new JSONObject();
        obj.put("key", key);
        obj.put("verCode", verCode);
        obj.put("img", specCaptcha.toBase64());

        return obj;
    }

    @RequestMapping("/captcha/gif")
    public JSONObject gif() {
        // gif类型
        GifCaptcha captcha = new GifCaptcha(130, 48);
        String verCode = captcha.text();
        String key = UUID.randomUUID().toString();

        // 存入redis并设置过期时间
        redisTemplate.opsForValue().set(key, verCode, 1, TimeUnit.MINUTES);

        JSONObject obj = new JSONObject();
        obj.put("key", key);
        obj.put("verCode", verCode);
        obj.put("img", captcha.toBase64());

        return obj;
    }

    @RequestMapping("/captcha/chinese")
    public JSONObject chinese() {
        // 中文类型
        ChineseCaptcha captcha = new ChineseCaptcha(130, 48);
        String verCode = captcha.text();
        String key = UUID.randomUUID().toString();

        // 存入redis并设置过期时间
        redisTemplate.opsForValue().set(key, verCode, 1, TimeUnit.MINUTES);

        JSONObject obj = new JSONObject();
        obj.put("key", key);
        obj.put("verCode", verCode);
        obj.put("img", captcha.toBase64());

        return obj;
    }

    @RequestMapping("/captcha/chineseGif")
    public JSONObject chineseGif() {
        // 中文gif类型
        ChineseGifCaptcha captcha = new ChineseGifCaptcha(130, 48);
        String verCode = captcha.text();
        String key = UUID.randomUUID().toString();

        // 存入redis并设置过期时间
        redisTemplate.opsForValue().set(key, verCode, 1, TimeUnit.MINUTES);

        JSONObject obj = new JSONObject();
        obj.put("key", key);
        obj.put("verCode", verCode);
        obj.put("img", captcha.toBase64());

        return obj;
    }

    @RequestMapping("/captcha/arithmetic")
    public JSONObject arithmetic() {
        // 算术类型
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(130, 48);
        captcha.setLen(3);  // 几个数运算，默认是两个（例如：3+2）
        captcha.getArithmeticString();  // 获取运算的公式：3+2=?
        //captcha.text();  // 获取运算的结果：5

        String verCode = captcha.text();
        String key = UUID.randomUUID().toString();

        // 存入redis并设置过期时间
        redisTemplate.opsForValue().set(key, verCode, 1, TimeUnit.MINUTES);

        JSONObject obj = new JSONObject();
        obj.put("key", key);
        obj.put("verCode", verCode);
        obj.put("img", captcha.toBase64());

        return obj;
    }
}
