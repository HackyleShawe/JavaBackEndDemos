package com.ks.demo.limiter.controller;

import com.ks.demo.limiter.algo.FixedWindowRateLimiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.Collections;

@RequestMapping("/fixedWindow")
@RestController
public class FixedWindowRateLimiterController {

    private final FixedWindowRateLimiter limiter = new FixedWindowRateLimiter(10, 10000L);

    @GetMapping("/byLocal")
    public String byLocal(HttpServletResponse response) {
        if (limiter.tryAcquire()) {
            return "限流通过";
        } else {
            response.setStatus(429);
            return "已达限流阈值";
        }
    }


    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @GetMapping("/byRedis")
    public String byRedis(HttpServletResponse response) {
        // 1.执行lua脚本
        DefaultRedisScript<Long> fixWindowsLua = new DefaultRedisScript<>();
        fixWindowsLua.setLocation(new ClassPathResource("lua/FixedWindowRateLimiter.lua"));
        fixWindowsLua.setResultType(Long.class);

        //Long res = redisTemplate.execute(fixWindowsLua, Collections.singletonList("fixedWindow-byRedis"), 10, 6);
        Long res = stringRedisTemplate.execute(fixWindowsLua, Collections.singletonList("rateLimiter:fixedWindow"), "10", "10");
        System.out.println("res " + res);
        if(res == 1) {
            return "限流通过";
        }else {
            response.setStatus(429);
            return "已达限流阈值";
        }
    }
}
