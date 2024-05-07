package com.jfzt.meeting.exception;

import com.jfzt.meeting.common.Result;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;



/**
 * 全局异常处理
 *
 * @author zilong.deng
 * @since 2024-04-30 10.13:51
 */
@Slf4j
@RestControllerAdvice(basePackages = "com.jfzt.meeting.controller")
public class GlobalExceptionControllerAdvice extends ResponseEntityExceptionHandler {



    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<String> handleAllExceptions(Exception e) {

        // 记录日志，处理异常信息
        return Result.fail(e.getMessage());
    }

    @ExceptionHandler(value = WxErrorException.class)
    @ResponseBody
    public Result<String> handleWxErrorException(WxErrorException e) {
        // 根据 WxErrorException 中的错误码和错误信息进行处理，返回友好的错误信息
        return Result.fail(String.valueOf(e.getError().getErrorCode()),e.getError().getErrorMsg());
    }
}
