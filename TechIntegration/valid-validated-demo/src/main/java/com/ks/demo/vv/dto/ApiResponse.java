package com.ks.demo.vv.dto;

import java.io.Serializable;

public class ApiResponse<T> implements Serializable {
    /** 状态：True-响应成功；False-响应失败 */
    private Boolean state;
    /** 响应码 */
    private Integer code;
    /** 响应消息 */
    private String message;
    /** 响应体 */
    private T data;

    public ApiResponse() {
        state = false;
    }

    public ApiResponse(Boolean state, Integer code, String message) {
        this.state = state;
        this.code = code;
        this.message = message;
    }

    public ApiResponse(Boolean state, Integer code, String message, T data) {
        this.state = state;
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResponse<T> success(Integer code, String message) {
        return new ApiResponse<T>(true, code, message);
    }

    public static <T> ApiResponse<T> success(Integer code, String message, T data) {
        return new ApiResponse<T>(true, code, message, data);
    }

    public static <T> ApiResponse<T> error(Integer code, String message) {
        return new ApiResponse<T>(false, code, message);
    }

    public static <T> ApiResponse<T> error(Integer code, String message, T data) {
        return new ApiResponse<T>(false, code, message, data);
    }

    public Boolean getState() {
        return state;
    }

    public void setState(Boolean state) {
        this.state = state;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
