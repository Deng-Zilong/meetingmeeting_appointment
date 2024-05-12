package com.jfzt.meeting.exception;


import com.jfzt.meeting.common.Result;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.apache.ibatis.exceptions.TooManyResultsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.security.NoSuchAlgorithmException;

import static com.jfzt.meeting.constant.MessageConstant.EXCEPTION_TYPE;
import static com.jfzt.meeting.exception.ErrorCodeEnum.SYSTEM_ERROR_B0001;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {


    /**
     * 处理自定义异常
     */
    @ExceptionHandler(RRException.class)
    public Result<String> handleRRException (RRException e) {
        log.error(e.getMessage(), e);
        Result<String> result = new Result<>();
        result.setCode(e.getCode());
        result.setMsg(e.getMessage());
        return result;
    }
    /**
     * 多结果异常
     */
    @ExceptionHandler(TooManyResultsException.class)
    public Result<String> TooManyRException(TooManyResultsException e){
        log.error(SYSTEM_ERROR_B0001.getDescription() + EXCEPTION_TYPE,e.getMessage());
        Result<String> result = new Result<>();
        result.setMsg(e.getMessage());
        return result;
    }

    /**
     * 处理token生成异常
     */
    @ExceptionHandler(NoSuchAlgorithmException.class)
    public Result<String> handleRRException (NoSuchAlgorithmException e) {
        log.error("accessToken生成异常", e);
        Result<String> result = new Result<>();
        result.setMsg("accessToken生成异常");
        return result;
    }

    /**
     * 调用企业微信接口异常
     */
    @ExceptionHandler(WxErrorException.class)
    public Result<String> handleRRException (WxErrorException e) {
        log.error(ErrorCodeEnum.SERVICE_ERROR_C0200.getCode(), ErrorCodeEnum.SERVICE_ERROR_C0200.getDescription());
        Result<String> result = new Result<>();
        result.setCode(ErrorCodeEnum.SERVICE_ERROR_C0200.getCode());
        result.setMsg(ErrorCodeEnum.SERVICE_ERROR_C0200.getDescription());
        return result;
    }

    /**
     * 处理其他未知异常
     *
     * @return {@code Result<String>}
     */
    @ExceptionHandler(Exception.class)
    public Result<String> handleException () {
        return Result.fail(ErrorCodeEnum.SYSTEM_ERROR_B0001);
    }


}
