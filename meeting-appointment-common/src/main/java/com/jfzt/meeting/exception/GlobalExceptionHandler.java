package com.jfzt.meeting.exception;


import com.jfzt.meeting.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.security.NoSuchAlgorithmException;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {


    /**
     * 处理自定义异常
     */
    @ExceptionHandler(RRException.class)
    public Result<String> handleRRException(RRException e){
        log.error(e.getMessage(), e);
        Result<String> result = new Result();
        result.setCode(e.getCode());
        result.setMsg(e.getMessage());
        return result;
    }


}
