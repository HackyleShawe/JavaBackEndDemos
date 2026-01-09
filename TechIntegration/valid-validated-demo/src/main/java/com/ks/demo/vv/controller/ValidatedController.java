package com.ks.demo.vv.controller;

import com.alibaba.fastjson2.JSON;
import com.ks.demo.vv.dto.ApiRequest;
import com.ks.demo.vv.dto.PersonAddDto;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Email;
import java.util.List;

@RequestMapping("/validated")
@RestController
@Validated
public class ValidatedController {
    //校验从@RequestBody来的实体，失败抛出:springframeword.MethodArgumentNotValidException
    @PostMapping("/requestBody")
    public String requestBody(@Validated @RequestBody PersonAddDto person) {
        return "通过校验" + " " + JSON.toJSONString(person);
    }

    //校验普通实体失败抛出：org.springframework.validation.BindException
    @PostMapping("/entity")
    public String entity(@Validated PersonAddDto person) {
        return "通过校验" + " " + JSON.toJSONString(person);
    }

    //校验普通参数失败抛出：javax.validation.ConstraintViolationException
    @PostMapping("/param")
    public String param(@Validated @Email @RequestParam("email") String email) {
        return "通过校验" + " " + email;
    }

    //数组与嵌套
    @PostMapping("/listNest")
    public String listNest(@Validated @RequestBody ApiRequest<List<PersonAddDto>> apiRequest) {
        return "通过校验" + " " + JSON.toJSONString(apiRequest);
    }


}
