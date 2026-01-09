package com.ks.demo.shiro.controller;

import com.ks.demo.shiro.entity.UserEntity;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class LoginController {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);

    @RequestMapping("/")
    public String redirectIndex() {
        //必须重定向，才能走到index方法中；如果不重定向，则直接转到视图中了
        return "redirect:/index";
    }

    @RequestMapping("/index")
    public ModelAndView index(ModelAndView modelAndView) {
        // 登录成后，即可通过Subject获取登录的用户信息
        UserEntity user = (UserEntity) SecurityUtils.getSubject().getPrincipal();

        if(user == null) {
            modelAndView.setViewName("login");
        } else {
            modelAndView.addObject("user", user);
            modelAndView.setViewName("index");
        }

        return modelAndView;
    }

    /**
     * 由视图解析器跳转到页面：login.html
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    public String login(String username, String password) {
        UsernamePasswordToken token = new UsernamePasswordToken(username, password);

        // 获取Subject对象
        Subject subject = SecurityUtils.getSubject();
        try {
            subject.login(token);
            return "redirect:/index"; //必须重定向，才能走到index方法中；如果不重定向，则直接转到视图中了
        } catch (Exception e) {
            LOGGER.error("登录验证失败：", e);
        }

        return "login";
    }
}
