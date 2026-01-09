package com.ks.demo.susi.service.sms;


import org.springframework.stereotype.Service;

import java.util.Random;

/**
 * 阿里云短信平台发送短信
 */
@Service
public class AliSendSmsService implements SendSms {
    @Override
    public String send(SendSmsDataDto sendSmsDataDto) {
        Random random = new Random();
        int rand = 0;
        do {
            rand = random.nextInt(999999);
        } while (rand <= 100000);
        String smsCode = String.valueOf(rand);
        System.out.println("阿里云短信平台向" +sendSmsDataDto.getPhone()+"发送短信验证码成功-smsCode=" + smsCode);
        return smsCode;
    }

}
