package com.hackyle.ssm.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/hello")
public class HelloController {

    @RequestMapping("test01")
    public String test01(Model model) {

        System.out.println("/hello/test01");

        model.addAttribute("msg", "kyle");

        return "success";
    }
}
