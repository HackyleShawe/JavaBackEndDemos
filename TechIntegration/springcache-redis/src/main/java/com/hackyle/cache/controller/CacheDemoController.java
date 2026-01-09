package com.hackyle.cache.controller;

import com.hackyle.cache.dto.UserinfoDto;
import com.hackyle.cache.service.CacheDemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CacheDemoController {
    @Autowired
    private CacheDemoService cacheDemoService;

    @GetMapping("/testCachePut")
    public String testCachePut() {
        UserinfoDto userinfo = new UserinfoDto();
        userinfo.setUid(111);
        userinfo.setName("Kyle");
        userinfo.setGender("man");

        return cacheDemoService.saveUser(userinfo);
    }

    @GetMapping("/testCacheEvict")
    public String testCacheEvict() {
        return cacheDemoService.deleteUser(111);
    }

    @GetMapping("/testCacheable")
    public String testCacheable() {
        return cacheDemoService.queryUserById(333);
    }
}
