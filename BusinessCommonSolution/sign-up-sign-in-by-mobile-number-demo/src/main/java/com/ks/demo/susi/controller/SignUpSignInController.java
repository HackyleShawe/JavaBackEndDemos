package com.ks.demo.susi.controller;

import com.ks.demo.susi.dto.SignUpSignInDto;
import com.ks.demo.susi.service.SignUpSignInService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 注册或登录Controller
 */
@RestController
public class SignUpSignInController {

    @Autowired
    private SignUpSignInService signUpSignInService;

    /**
     * 发送短信验证码
     */
    @PostMapping("/sendSmsCode")
    public String sendSmsCode(@RequestBody SignUpSignInDto signUpSignInDto) {
        String mobilePhone = signUpSignInDto.getMobilePhone();
        String verifyCode = signUpSignInDto.getVerifyCode();
        String uuid = signUpSignInDto.getUuid();
        if(StringUtils.isEmpty(mobilePhone) || StringUtils.isEmpty(verifyCode) || StringUtils.isEmpty(uuid)) {
            return "入参不合法";
        }
        //注意，电话号码应该进行格式检查与正则校验

        return signUpSignInService.sendSmsCode(signUpSignInDto);
    }


    /**
     * 通过手机号实现注册或登录
     */
    @PostMapping("/signUpSignInByMobileNumber")
    public String signUpSignInByMobileNumber(@RequestBody SignUpSignInDto signUpSignInDto) {
        String mobilePhone = signUpSignInDto.getMobilePhone(); //注意，电话号码应该进行格式检查与正则校验
        //String verifyCode = signUpSignInDto.getVerifyCode();
        //String uuid = signUpSignInDto.getUuid();
        String smsCode = signUpSignInDto.getSmsCode();
        if(StringUtils.isEmpty(mobilePhone) || StringUtils.isEmpty(smsCode)) {
            return "入参不合法";
        }
        //注意，电话号码应该进行格式检查与正则校验

        return signUpSignInService.signUpSignInByMobileNumber(signUpSignInDto);
    }
}
