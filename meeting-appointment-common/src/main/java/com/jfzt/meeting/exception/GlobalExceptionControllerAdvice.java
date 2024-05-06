package com.jfzt.meeting.exception;

import com.jfzt.meeting.common.Result;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@Slf4j
@RestControllerAdvice(basePackages = "com.jfzt.meeting.controller")
public class GlobalExceptionControllerAdvice extends ResponseEntityExceptionHandler {


    @ExceptionHandler(value = Exception.class)
    public Result handleException(Exception exception) {
        log.error("错误：", exception);
        return Result.fail("报错");
    }

    @ExceptionHandler(value = WxErrorException.class)
    @ResponseBody
    public String handleWxErrorException(WxErrorException e) {
        // 根据 WxErrorException 中的错误码和错误信息进行处理，返回友好的错误信息
        return "微信 API 调用失败，错误码：" + e.getError().getErrorCode() + "，错误信息：" + e.getError().getErrorMsg();
    }
}
