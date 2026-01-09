package com.ks.demo.susi.service.sms;

public class SendSmsDataDto {
    /** 电话 */
    private String phone;
    /** 短信签名内容，必须填写已审核通过的签名 */
    private String signName;
    /** 模板 ID: 必须填写已审核通过的模板 ID */
    private String templateId;
    /** 模板参数: 模板参数的个数需要与 TemplateId 对应模板的变量个数保持一致，若无模板参数，则设置为空 */
    private String[] templateParamSet;
    /** 应用id */
    private String smsSdkAppId;
    /** 云api密钥中的 secretId */
    private String secretId;
    /** 云api密钥中的 secretKey */
    private String secretKey;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSignName() {
        return signName;
    }

    public void setSignName(String signName) {
        this.signName = signName;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public String[] getTemplateParamSet() {
        return templateParamSet;
    }

    public void setTemplateParamSet(String[] templateParamSet) {
        this.templateParamSet = templateParamSet;
    }

    public String getSmsSdkAppId() {
        return smsSdkAppId;
    }

    public void setSmsSdkAppId(String smsSdkAppId) {
        this.smsSdkAppId = smsSdkAppId;
    }

    public String getSecretId() {
        return secretId;
    }

    public void setSecretId(String secretId) {
        this.secretId = secretId;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }
}
