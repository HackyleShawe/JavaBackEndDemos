package com.ks.demo.vv.controller;

import com.alibaba.fastjson.JSON;
import com.ks.demo.vv.dto.ApiRequest;
import com.ks.demo.vv.dto.ApiResponse;
import com.ks.demo.vv.dto.PersonAddDto;
import com.ks.demo.vv.dto.ValidList;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RequestMapping("/valid")
@RestController
public class ValidController {
    private String errorInfo(BindingResult br) {
        StringBuilder sb = new StringBuilder();
        br.getAllErrors().forEach(ele -> sb.append(ele.getDefaultMessage()).append("；"));
        return sb.toString();
    }

    /**
     * 校验表单实体
     * 关键语法
     *  - 在Controller层使用”@Valid”修饰实体类参数
     *  - 在实体类中使用校验注解
     *  - 使用BindingResult接收校验结果
     */
    @PostMapping("/addByFrom")
    private ApiResponse<String> addByFrom(@Valid PersonAddDto personAddDtoDto, BindingResult br) {
        if(br.hasErrors()) {
            return ApiResponse.error(9999, "请求参数校验不通过", errorInfo(br));
        }

        System.out.println(JSON.toJSONString(personAddDtoDto));

        return ApiResponse.success(2000, "成功");
    }


    /**
     * 校验失效，不支持检验List中的实体
     */
    @PostMapping("/addList")
    private ApiResponse<String> addList(@RequestBody @Valid List<PersonAddDto> personDtoList, BindingResult br) {
        if(br.hasErrors()) {
            return ApiResponse.error(9999, "请求参数校验不通过", errorInfo(br));
        }

        System.out.println(JSON.toJSONString(personDtoList));

        return ApiResponse.success(2000, "成功");
    }
    /**
     * 解决校验List中的实体：使用一个对象包装一层List，其本质是"嵌套校验"
     * 最佳的解决方案是下文将要介绍的将请求体以ApiRequest封装，在"T data"上使用@Valid修饰
     */
    @PostMapping("/addListByNest")
    private ApiResponse<String> addByList(@RequestBody @Valid ValidList<PersonAddDto> personDtoList, BindingResult br) {
        if(br.hasErrors()) {
            return ApiResponse.error(9999, "请求参数校验不通过", errorInfo(br));
        }

        System.out.println(JSON.toJSONString(personDtoList));

        return ApiResponse.success(2000, "成功");
    }


    /**
     * 嵌套检测
     * 重点关注ApiRequest中"private T data;"上的"@Valid"
     */
    @PostMapping("/addByObjNest")
    private ApiResponse<String> addByObjNest(@RequestBody @Valid ApiRequest<PersonAddDto> personDto, BindingResult br) {
        if(br.hasErrors()) {
            return ApiResponse.error(9999, "请求参数校验不通过", errorInfo(br));
        }

        System.out.println(JSON.toJSONString(personDto));
        return ApiResponse.success(2000, "成功");
    }

    /**
     * 嵌套List
     */
    @PostMapping("/addByListNest")
    private ApiResponse<String> addByListNest(@RequestBody @Valid ApiRequest<List<PersonAddDto>> personDto, BindingResult br) {
        if(br.hasErrors()) {
            return ApiResponse.error(9999, "请求参数校验不通过", errorInfo(br));
        }

        System.out.println(JSON.toJSONString(personDto));
        return ApiResponse.success(2000, "成功");
    }


}
