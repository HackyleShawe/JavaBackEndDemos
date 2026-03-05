package com.ks.demo.datatime.infrastructure;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.time.DateTimeException;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * ControllerAdvice表示这是一个控制器增强类，当控制器发生异常且符合类中定义的拦截异常类，将会被拦截。
 * 在捕获异常时是按照异常方法的顺序依次捕获(类似于catch关键字后的捕获顺序)，所以需要将顶级的异常放在最后
 * 注意：各个异常处理方法要同名，采取重载的形式
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * URL或查询参数(@RequestParam、@PathVariable)中的时间，解析失败抛出异常
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ApiResponse<?> handleTimeParseException(MethodArgumentTypeMismatchException ex) {
        log.error("出现MethodArgumentTypeMismatchException异常：", ex);
        return ApiResponse.fail(HttpStatus.BAD_REQUEST.value(), "请求参数中时间格式错误");
    }

    /**
     * 请求体（如JSON）中的时间，解析失败时抛出异常
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ApiResponse<?> handleJsonParseException(HttpMessageNotReadableException ex) {
        log.error("出现HttpMessageNotReadableException异常：", ex);
        return ApiResponse.fail(HttpStatus.BAD_REQUEST.value(), "请求体中的时间格式错误");
    }

    /**
     * 捕获时间格式化失败的异常
     */
    @ExceptionHandler(DateTimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<?> handleDateTimeException(DateTimeException ex, TypeMismatchException tme) {
        String msg = "";
        if(ex != null) {
            msg += ex.getMessage();
        }
        if(tme != null) {
            msg += "; " + tme.getMessage();
        }
        log.error("DateTimeParseException异常：", ex);

        return ApiResponse.fail(HttpStatus.BAD_REQUEST.value(), "时间解析出错");
    }

    /**
     * 捕获参数校验注解失败抛出的异常：MethodArgumentNotValidException-Spring封装的参数验证异常处理
     * <p>MethodArgumentNotValidException：作用于 @Validated @Valid 注解，接收参数加上@RequestBody注解（json格式）才会有这种异常。</p>
     *
     * @param e MethodArgumentNotValidException异常信息
     * @return 响应数据
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ApiResponse<?> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors()
                .stream()
                .map(n -> String.format("%s: %s", n.getField(), n.getDefaultMessage()))
                .reduce((x, y) -> String.format("%s; %s", x, y))
                .orElse("参数输入有误");
        log.error("MethodArgumentNotValidException异常，参数校验异常：{}", msg);
        return ApiResponse.fail(HttpStatus.BAD_REQUEST.value(), msg);
    }

    /**
     * 捕获参数校验注解失败抛出的异常：ConstraintViolationException-jsr规范中的验证异常，嵌套检验问题
     * <p>ConstraintViolationException：作用于 @NotBlank @NotNull @NotEmpty 注解，校验单个String、Integer、Collection等参数异常处理。</p>
     * <p>注：Controller类上必须添加@Validated注解，否则接口单个参数校验无效</p>
     *
     * @param e ConstraintViolationException异常信息
     * @return 响应数据
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = ConstraintViolationException.class)
    public ApiResponse<?> constraintViolationExceptionHandler(ConstraintViolationException e) {
        String msg = e.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("; "));

        log.error("ConstraintViolationException，参数校验异常：{}", msg);
        return ApiResponse.fail(HttpStatus.BAD_REQUEST.value(), msg);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler(IllegalArgumentException.class)
    public ApiResponse<Exception> badRequestExceptionHandler(HttpServletRequest request, IllegalArgumentException exception) {
        log.error("出现IllegalArgumentException异常：", exception);
        return ApiResponse.fail(HttpStatus.BAD_REQUEST.value(), "参数错误");
    }


    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    @ExceptionHandler(Exception.class)
    public ApiResponse<Exception> exceptionHandler(HttpServletRequest request, Exception exception) {
        log.error("出现Exception异常：", exception);
        //不要直接把exception.getMessage()抛给前端，可能包含整个异常栈信息，造成信息泄露
        return ApiResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(), "服务器内部错误");
    }

}
