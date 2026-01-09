package com.ks.demo.vv.config;

import com.ks.demo.vv.dto.ApiResponse;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Validator校验RequestBody实体不通过抛出的异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<String> methodArgumentNotValidException(MethodArgumentNotValidException e) {
        //LOGGER.info("全局异常捕获器-捕获到MethodArgumentNotValidException：", e);
        return ApiResponse.error(9999, "校验RequestBody的实体不通过"); //这里为了代码方便，就不放于枚举类了
    }
    /**
     * Validator校验单个参数校验失败抛出的异常
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ApiResponse<String> constraintViolationException(ConstraintViolationException e) {
        //LOGGER.info("全局异常捕获器-捕获到ConstraintViolationException：", e);
        return ApiResponse.error(9999, "处理单个参数校验失败抛出的异常");
    }
    /**
     * Validator校验普通实体失败抛出的异常
     */
    @ExceptionHandler(BindException.class)
    public ApiResponse<String> bindException(BindException e) {
        //LOGGER.info("全局异常捕获器-捕获到BindException：", e);
        return ApiResponse.error(9999, "校验普通实体失败抛出的异常");
    }

    //其他异常

    /**
     * 总异常：只要出现异常，总会被这个拦截，因为所以的异常父类为：Exception
     */
    @ExceptionHandler(Exception.class)
    public ApiResponse<String> exception(Exception e) {
        //LOGGER.info("全局异常捕获器-捕获到Exception：", e);
        return ApiResponse.error(9999, "总异常");
    }
}
