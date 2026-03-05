package com.ks.demo.datatime.infrastructure;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class ApiResponse<T> implements Serializable {
    /**
     * 响应码
     */
    private Integer code;
    /**
     * 响应消息
     */
    private String msg;
    /**
     * 响应体
     */
    private T data;

    public static <T> ApiResponse<T> ok() {
        return new ApiResponse<T>(ResponseEnum.OP_OK.getCode(), ResponseEnum.OP_OK.getMessage(), null);
    }
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<T>(ResponseEnum.OP_OK.getCode(), ResponseEnum.OP_OK.getMessage(), data);
    }
    public static <T> ApiResponse<T> ok(String message, T data) {
        return new ApiResponse<T>(ResponseEnum.OP_OK.getCode(), message, data);
    }

    public static <T> ApiResponse<T> fail() {
        return new ApiResponse<T>(ResponseEnum.OP_FAIL.getCode(), ResponseEnum.OP_FAIL.getMessage(), null);
    }
    public static <T> ApiResponse<T> fail(String message) {
        return new ApiResponse<T>(ResponseEnum.OP_FAIL.getCode(), message, null);
    }
    public static <T> ApiResponse<T> fail(Integer code, String message) {
        return new ApiResponse<T>(code, message, null);
    }
    public static <T> ApiResponse<T> fail(String message, T data) {
        return new ApiResponse<T>(ResponseEnum.OP_FAIL.getCode(), message, data);
    }
    public static <T> ApiResponse<T> fail(Integer code, String message, T data) {
        return new ApiResponse<T>(code, message, data);
    }


}
