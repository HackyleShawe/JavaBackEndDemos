package com.ks.kaptcha.controller;

import com.ks.kaptcha.service.KaptchaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
public class KaptchaController {
    @Autowired
    private KaptchaService kaptchaService;

    /**
     * 验证码图片响应为Base64
     * 1、后端生成验证码图片的Base64、以及该验证码的唯一表示uuid，存于Redis
     * 2、前端请求将图片的Base64和uuid传递过去
     * 3、前端将用户输入的验证码和uuid传来，后端从Redis中取出，进行比对
     */
    @GetMapping("/codeByBase64")
    public Map<String, String> codeByBase64() {
        return kaptchaService.codeByBase64();
    }

    /**
     * 验证码图片响应为Stream
     * 1、前端请求该接口，携带一个uuid，表明本次生成验证码的唯一标识
     * 2、后端生成验证码图片，以流的形式响应给前端，并将验证码信息存于Redis
     * 3、前端将用户输入的验证码和uuid传来，后端从Redis中取出，进行比对
     */
    @GetMapping("/codeByStream")
    public void codeByStream(HttpServletRequest request, HttpServletResponse response) {
        //uuid，本次请求生成验证码的唯一标识code
        String uuid = request.getParameter("uuid");
        if(uuid != null && !"".equals(uuid.trim())) {
            kaptchaService.codeByStream(uuid, response);
        }
    }

    @GetMapping("/checkCode")
    public String checkCode(HttpServletRequest request) {
        String uuid = request.getParameter("uuid");
        String code = request.getParameter("code");

        boolean checkResult = kaptchaService.checkCode(uuid, code);
        return checkResult?"验证成功":"验证失败";
    }
}
