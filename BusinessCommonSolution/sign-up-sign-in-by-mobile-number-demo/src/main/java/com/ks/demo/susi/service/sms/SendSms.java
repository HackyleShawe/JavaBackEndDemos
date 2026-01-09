package com.ks.demo.susi.service.sms;

/**
 * 发送短信验证码
 */
public interface SendSms {
    /**
     *
     * @param sendSmsDataDto 短信发送的一系列参数
     * @return 短信验证码
     */
    String send(SendSmsDataDto sendSmsDataDto);
}
