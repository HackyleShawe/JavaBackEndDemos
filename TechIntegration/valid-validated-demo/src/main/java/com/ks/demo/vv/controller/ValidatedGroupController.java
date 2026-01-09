package com.ks.demo.vv.controller;

import com.alibaba.fastjson2.JSON;
import com.ks.demo.vv.config.ValidatedGroup;
import com.ks.demo.vv.dto.ApiRequest;
import com.ks.demo.vv.dto.PersonAddDto;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Email;
import java.util.List;

@RequestMapping("/validated")
@RestController
public class ValidatedGroupController {

    /**
     * `@Validated`中不指定任何分组
     */
    @PostMapping("/groupAdd")
    public String groupAdd(@Validated @RequestBody ApiRequest<PersonAddDto> apiRequest) {
        return "通过校验" + " " + JSON.toJSONString(apiRequest);
    }

    /**
     * `@Validated`中限定使用'ValidatedGroup.Update.class'分组
     * PersonAddDto实体类中的所有加了该个分组标识的校验都生效
     */
    @PostMapping("/groupUpdate")
    public String groupUpdate(@Validated(ValidatedGroup.Update.class) @RequestBody ApiRequest<PersonAddDto> apiRequest) {
        return "通过校验" + " " + JSON.toJSONString(apiRequest);
    }

    /**
     * `@Validated`中限定'ValidatedGroup.Delete.class'分组
     * PersonAddDto实体类中的所有加了该个分组标识的校验都生效
     */
    @PostMapping("/groupDel")
    public String groupDel(@Validated(ValidatedGroup.Delete.class) @RequestBody ApiRequest<PersonAddDto> apiRequest) {
        return "通过校验" + " " + JSON.toJSONString(apiRequest);
    }
}
