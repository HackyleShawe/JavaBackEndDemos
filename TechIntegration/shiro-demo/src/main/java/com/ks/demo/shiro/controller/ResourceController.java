package com.ks.demo.shiro.controller;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/resource")
public class ResourceController {

    @RequiresPermissions("resource:create")
    @GetMapping("/create")
    public String create() {
        return "看到这句话，表明你拥有/resource/create的这种权限";
    }

    @RequiresPermissions("resource:delete")
    @GetMapping("/delete")
    public String delete() {
        return "看到这句话，表明你拥有/resource/delete的这种权限";
    }

    @RequiresPermissions("resource:update")
    @GetMapping("/update")
    public String update() {
        return "看到这句话，表明你拥有/resource/update的这种权限";
    }

    @RequiresPermissions("resource:read")
    @GetMapping("/read")
    public String read() {
        return "看到这句话，表明你拥有/resource/read的这种权限";
    }
}
