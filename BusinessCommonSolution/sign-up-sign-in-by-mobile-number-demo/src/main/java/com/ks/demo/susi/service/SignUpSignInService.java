package com.ks.demo.susi.service;

import com.ks.demo.susi.dto.SignUpSignInDto;
import com.ks.demo.susi.entity.SmsRecordEntity;
import com.ks.demo.susi.entity.UserEntity;
import com.ks.demo.susi.service.sms.SendSmsDataDto;
import com.ks.demo.susi.service.sms.TencentSendSmsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class SignUpSignInService {
    @Autowired
    private KaptchaService kaptchaService;
    @Autowired
    private TencentSendSmsService tencentSendSmsService;
    @Autowired
    private UserService userService;
    @Autowired
    private SmsRecordService smsRecordService;
    @Autowired
    private ValueOperations<String, String> redisValueOperations;

    private static final int SMS_REQUEST_COUNT_LIMIT = 2; //短信发送次数限制
    private static final int SMS_REQUEST_COUNT_TIME = 10; //短信发送的时间限制
    private static final String SMS_REQUEST_COUNT_PRE = "SMS_REQUEST_COUNT_";
    private static final String SMS_CODE_PRE = "SMS_CODE_";


    /**
     * 发送短信验证码
     * 1.检查图形验证码，检查通过才进行后续逻辑
     * 2.检查该个手机号在某个时间内是否超过的发送次数
     * 3.发送短信验证码，缓存，记录相关日志
     */
    public String sendSmsCode(SignUpSignInDto signUpSignInDto) {
        String mobilePhone = signUpSignInDto.getMobilePhone();
        String verifyCode = signUpSignInDto.getVerifyCode();
        String uuid = signUpSignInDto.getUuid();

        String res = "";
        boolean checked = kaptchaService.checkCode(uuid, verifyCode);
        if(checked) { //图形验证码检查通过，可以发送短信
            //限制一段时间内短信验证码的请求次数（短信发送次数）
            //例如：如10分钟内5次请求，Key=SMS_REQUEST_COUNT_手机号，Value=剩余次数
            String requestCount = redisValueOperations.get(SMS_REQUEST_COUNT_PRE + mobilePhone);
            if(StringUtils.isEmpty(requestCount)) {
                redisValueOperations.set(SMS_REQUEST_COUNT_PRE+mobilePhone, "1", SMS_REQUEST_COUNT_TIME, TimeUnit.MINUTES);
            } else {
                int count = Integer.parseInt(requestCount) + 1;
                if(count > SMS_REQUEST_COUNT_LIMIT) {
                    return SMS_REQUEST_COUNT_TIME+"分钟内限制发送"+SMS_REQUEST_COUNT_LIMIT+"次短信验证码，请稍后重试";
                }
                redisValueOperations.set(SMS_REQUEST_COUNT_PRE+mobilePhone, String.valueOf(count));
            }

            SendSmsDataDto sendSmsDataDto = new SendSmsDataDto();
            sendSmsDataDto.setPhone(mobilePhone);
            String smsCode = tencentSendSmsService.send(sendSmsDataDto);
            if(StringUtils.isNotEmpty(smsCode)) {
                //缓存：以mobilePhone作为Key，后续在校验smsCode的时候，根据手机号获取
                redisValueOperations.set(SMS_CODE_PRE+mobilePhone, smsCode, 60L, TimeUnit.SECONDS);

                SmsRecordEntity smsRecordEntity = new SmsRecordEntity();
                smsRecordEntity.setMobilePhone(mobilePhone);
                smsRecordEntity.setSmsCode(smsCode);
                smsRecordEntity.setStatus("已发送");
                //记录短信发送
                smsRecordService.record(smsRecordEntity);

                res = "图形验证码检查成功，发送短信成功（smsCode=" +smsCode+ "）";
            } else {
                res = "图形验证码检查成功，发送短信失败，请重试";
            }

        } else { //图形验证码检查失败，不能发送短信
            res = "图形验证码检查失败，请重试";
        }

        return res;
    }

    /**
     * 根据手机号进行登录，如果没有注册则自动进行注册
     */
    public String signUpSignInByMobileNumber(SignUpSignInDto signUpSignInDto) {
        String mobilePhone = signUpSignInDto.getMobilePhone();
        String smsCode = signUpSignInDto.getSmsCode();
        String res = "";

        String smsCodeRedis = redisValueOperations.get(SMS_CODE_PRE+mobilePhone);
        //注意：这里不要立即删除该电话号码对应的短信验证码，因为发送一次短信是非常珍贵的资源，
        //    校验短信验证码不通过可能是用户输入时看错了，给用户再次输入的机会
        //redisValueOperations.getOperations().delete(mobilePhone);

        SmsRecordEntity smsRecordEntity = new SmsRecordEntity();
        smsRecordEntity.setMobilePhone(mobilePhone);
        smsRecordEntity.setStatus("已使用，已过期");
        smsRecordService.updateStatus(smsRecordEntity);
        if(smsCode.equalsIgnoreCase(smsCodeRedis)) {
            System.out.println("手机号mobilePhone="+mobilePhone+"，校验短信验证码通过");
        } else {
            res = "手机号"+mobilePhone+"短信验证码校验不通过";
            return res;
        }

        //检查是否已经注册
        boolean signUp = userService.checkSignUp(mobilePhone);
        if(signUp) { //已经注册，校验短信验证码后直接登录直接登录
            //其他登录逻辑

            res = "短信验证码校验通过，已经注册，登录成功";

        } else { //进行注册，再登录
            UserEntity userEntity = new UserEntity();
            userEntity.setMobilePhone(mobilePhone);
            boolean signUpSuccess = userService.signUp(userEntity);
            if(signUpSuccess) { //注册成功
                //其他登录逻辑

                res = "短信验证码校验通过，注册成功，并登录成功";

            } else {
                res = "短信验证码校验通过，注册失败";
            }
        }

        return res;
    }
}
