package com.ks.demo.ec.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.wf.captcha.SpecCaptcha;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
public class EasyCaptchaController {
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 生成验证码
     */
    @RequestMapping("/captcha")
    public String captcha() throws Exception {
        SpecCaptcha specCaptcha = new SpecCaptcha(130, 48, 5);
        String verCode = specCaptcha.text().toLowerCase();
        String key = UUID.randomUUID().toString();

        // 存入redis并设置过期时间
        redisTemplate.opsForValue().set(key, verCode, 1, TimeUnit.MINUTES);

        JSONObject obj = new JSONObject();
        obj.put("key", key);
        obj.put("verCode", verCode);
        obj.put("img", specCaptcha.toBase64());

        return JSON.toJSONString(obj);
    }

    /**
     * 验证验证码
     */
    @GetMapping("/login")
    public String login(String username, String password, String key, String verCode){
        String val = String.valueOf(redisTemplate.opsForValue().get(key));
        if(null != val && !"".equals(val.trim()) && val.equalsIgnoreCase(verCode)) {
            return "验证码正确";
        }

        return "验证码不正确";
    }
}




