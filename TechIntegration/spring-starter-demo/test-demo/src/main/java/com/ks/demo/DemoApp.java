package com.ks.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import redis.clients.jedis.Jedis;

@SpringBootApplication
public class DemoApp {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(DemoApp.class, args);

        Jedis jedis = (Jedis)context.getBean("jedis");
        System.out.println(jedis);

        jedis.set("name", "hackyle");
        String name = jedis.get("name");
        System.out.println(name);
    }
}
