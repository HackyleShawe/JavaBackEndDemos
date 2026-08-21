package com.ks.demo.limiter.controller;

import com.ks.demo.limiter.algo.TokenBucketRateLimiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.Collections;

@RequestMapping("/tokenBucket")
@RestController
public class TokenBucketRateLimiterController {
    //令牌桶里初始有3个，后续1s补充1个
    TokenBucketRateLimiter tokenBucketRateLimiter = new TokenBucketRateLimiter(3, 1);

    @GetMapping("/byLocal")
    public String byLocal() {
        if(tokenBucketRateLimiter.tryAcquire()) {
            return "tokenBucket byLocal限流通过";
        } else {
            return "tokenBucket byLocal已达限流阈值";
        }
    }

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @GetMapping("/byRedis")
    public String byRedis(HttpServletResponse response) {
        // 1.执行lua脚本
        DefaultRedisScript<Long> fixWindowsLua = new DefaultRedisScript<>();
        fixWindowsLua.setLocation(new ClassPathResource("lua/TokenBucketRateLimiter.lua"));
        fixWindowsLua.setResultType(Long.class);

        //Long res = redisTemplate.execute(fixWindowsLua, Collections.singletonList("fixedWindow-byRedis"), 10, 6);
        Long res = stringRedisTemplate.execute(fixWindowsLua, Collections.singletonList(
                "rateLimiter:tokenBucket"),
                //令牌桶里初始有3个，后续1s补充1个
                "3", "1", System.currentTimeMillis()+"");
        System.out.println("res " + res);
        if(res == 1) {
            return "tokenBucket byRedis限流通过";
        }else {
            response.setStatus(429);
            return "tokenBucket byRedis已达限流阈值";
        }
    }
}
