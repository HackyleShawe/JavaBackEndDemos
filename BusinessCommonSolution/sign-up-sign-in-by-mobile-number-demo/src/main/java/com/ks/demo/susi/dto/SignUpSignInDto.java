package com.ks.demo.susi.dto;

public class SignUpSignInDto {
    /** 手机号 */
    private String mobilePhone;
    /** 图形验证码 */
    private String verifyCode;
    /** 图形验证码的唯一标识 */
    private String uuid;
    /** 短信验证码 */
    private String smsCode;

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getVerifyCode() {
        return verifyCode;
    }

    public void setVerifyCode(String verifyCode) {
        this.verifyCode = verifyCode;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getSmsCode() {
        return smsCode;
    }

    public void setSmsCode(String smsCode) {
        this.smsCode = smsCode;
    }
}
