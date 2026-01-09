package com.ks.demo.vv.dto;

import javax.validation.Valid;
import java.io.Serializable;

/**
 * API的请求封装
 */
public class ApiRequest<T> implements Serializable {
    /** 接入Key */
    private String appKey;
    /** 接入秘钥 */
    private String appSecret;
    /** 请求Trace */
    private String trace;
    /** 请求体 */
    @Valid
    private T data;

    public ApiRequest() {
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public String getTrace() {
        return trace;
    }

    public void setTrace(String trace) {
        this.trace = trace;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
