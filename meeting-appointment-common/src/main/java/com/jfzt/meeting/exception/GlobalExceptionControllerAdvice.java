package com.jfzt.meeting.exception;

import com.jfzt.meeting.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 集中处理所有异常
 */
/**
 * 集中处理所有异常
 *
 * @author zhenxing.lu
 * @since 2024-04-30 10.13:51
 */
@Slf4j
@RestControllerAdvice(basePackages = "com.jfzt.meeting.controller")
public class GlobalExceptionControllerAdvice {


    @ExceptionHandler(value = Exception.class)
    public Result handleException(Exception exception) {
        log.error("错误：", exception);
        return Result.fail("报错");
    }

}
