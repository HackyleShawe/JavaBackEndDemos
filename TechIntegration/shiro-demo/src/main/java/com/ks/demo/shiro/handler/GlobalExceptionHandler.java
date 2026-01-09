package com.ks.demo.shiro.handler;

import org.apache.shiro.authz.AuthorizationException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Order(value = Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler {
    /**
     * 捕获Shiro的没有权限（AuthorizationException）异常
     */
    @ExceptionHandler(value = AuthorizationException.class)
    public String handleAuthorizationException() {
        return "403";
    }

    /**
     * 捕获Exception这个异常，跳转到error.html
     */
    @ExceptionHandler(value = Exception.class)
    public String handleException() {
        return "error";
    }
}
