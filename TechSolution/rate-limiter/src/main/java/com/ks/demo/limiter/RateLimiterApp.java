package com.ks.demo.limiter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RateLimiterApp {
    public static void main(String[] args) {
        SpringApplication.run(RateLimiterApp.class, args);
    }

}
