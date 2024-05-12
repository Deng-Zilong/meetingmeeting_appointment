package com.jfzt.meeting.exception;


import com.jfzt.meeting.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;




@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public Result<String> handleException(Exception e){
        log.error(e.getMessage()+"----------------------------------------"+e);
        return Result.fail(e.getMessage());
    }

}
