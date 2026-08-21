package com.ks.demo.limiter.controller;

import com.ks.demo.limiter.algo.LeakyBucketRateLimiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.Collections;

@RequestMapping(("/leakyBucket"))
@RestController
public class LeakyBucketRateLimiterController {
    //1s内至多3次，流出速率为1s 1个
    LeakyBucketRateLimiter leakyBucketRateLimiter = new LeakyBucketRateLimiter(3, 1);

    @GetMapping("/byLocal")
    public String byLocal() {
        if(leakyBucketRateLimiter.tryAcquire()) {
            return "限流通过";
        } else {
            return "已达限流阈值";
        }
    }

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @GetMapping("/byRedis")
    public String byRedis(HttpServletResponse response) {
        // 1.执行lua脚本
        DefaultRedisScript<Long> fixWindowsLua = new DefaultRedisScript<>();
        fixWindowsLua.setLocation(new ClassPathResource("lua/LeakyBucketRateLimiter.lua"));
        fixWindowsLua.setResultType(Long.class);

        //Long res = redisTemplate.execute(fixWindowsLua, Collections.singletonList("fixedWindow-byRedis"), 10, 6);
        Long res = stringRedisTemplate.execute(fixWindowsLua, Collections.singletonList("rateLimiter:leakyBucket"),
                //1s内至多3次，流出速率为1s 1个
                "3", "1", System.currentTimeMillis()+"");
        System.out.println("res " + res);
        if(res == 1) {
            return "限流通过";
        }else {
            response.setStatus(429);
            return "已达限流阈值";
        }
    }
}
