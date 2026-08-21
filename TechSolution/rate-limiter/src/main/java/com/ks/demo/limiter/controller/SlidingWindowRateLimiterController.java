package com.ks.demo.limiter.controller;

import com.ks.demo.limiter.algo.FixedWindowRateLimiter;
import com.ks.demo.limiter.algo.SlidingWindowRateLimiterV1;
import com.ks.demo.limiter.algo.SlidingWindowRateLimiterV2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.Collections;

@RequestMapping("/slidingWindow")
@RestController
public class SlidingWindowRateLimiterController {
    private final SlidingWindowRateLimiterV1 limiterV1 = new SlidingWindowRateLimiterV1(10, 10000L, 10);
    private final SlidingWindowRateLimiterV2 limiterV2 = new SlidingWindowRateLimiterV2(10, Duration.ofMillis(10000L));


    @GetMapping("/byLocalV1")
    public String byLocal(HttpServletResponse response) {
        if (limiterV1.tryAcquire()) {
            return "限流通过";
        } else {
            response.setStatus(429);
            return "已达限流阈值";
        }
    }

    @GetMapping("/byLocalV2")
    public String byLocalV2(HttpServletResponse response) {
        if (limiterV2.tryAcquire()) {
            return "限流通过";
        } else {
            response.setStatus(429);
            return "已达限流阈值";
        }
    }

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @GetMapping("/byZsetV1")
    public String byZsetV1(HttpServletResponse response) {
        DefaultRedisScript<Long> fixWindowsLua = new DefaultRedisScript<>();
        fixWindowsLua.setLocation(new ClassPathResource("lua/SlidingWindowRateLimiterByZsetV1.lua"));
        fixWindowsLua.setResultType(Long.class);

        //Long res = redisTemplate.execute(fixWindowsLua, Collections.singletonList("fixedWindow-byRedis"), 10, 6);
        Long res = stringRedisTemplate.execute(fixWindowsLua, Collections.singletonList("rateLimiter:slidingWindow:byZsetV1"), "10", "10");
        System.out.println("res " + res);
        if (res == 1) {
            return "限流通过";
        } else {
            response.setStatus(429);
            return "已达限流阈值";
        }
    }

    @GetMapping("/byZsetV2")
    public String byZsetV2(HttpServletResponse response) {
        DefaultRedisScript<Long> fixWindowsLua = new DefaultRedisScript<>();
        fixWindowsLua.setLocation(new ClassPathResource("lua/SlidingWindowRateLimiterByZsetV2.lua"));
        fixWindowsLua.setResultType(Long.class);

        //Long res = redisTemplate.execute(fixWindowsLua, Collections.singletonList("fixedWindow-byRedis"), 10, 6);
        Long res = stringRedisTemplate.execute(fixWindowsLua, Collections.singletonList("rateLimiter:slidingWindow:byZsetV2"), "10", "10");
        System.out.println("res " + res);
        if (res == 1) {
            return "限流通过";
        } else {
            response.setStatus(429);
            return "已达限流阈值";
        }
    }

    @GetMapping("/byHash")
    public String byHash(HttpServletResponse response) {
        DefaultRedisScript<Long> fixWindowsLua = new DefaultRedisScript<>();
        fixWindowsLua.setLocation(new ClassPathResource("lua/SlidingWindowRateLimiterByHash.lua"));
        fixWindowsLua.setResultType(Long.class);

        //Long res = redisTemplate.execute(fixWindowsLua, Collections.singletonList("fixedWindow-byRedis"), 10, 6);
        Long res = stringRedisTemplate.execute(fixWindowsLua, Collections.singletonList("rateLimiter:slidingWindow:byHash"), "10", "10");
        System.out.println("res " + res);
        if (res == 1) {
            return "限流通过";
        } else {
            response.setStatus(429);
            return "已达限流阈值";
        }
    }
}
