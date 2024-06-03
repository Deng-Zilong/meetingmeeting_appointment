package com.jfzt.meeting.exception;


import com.jfzt.meeting.common.Result;
import com.jfzt.meeting.weixin.AesException;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.apache.ibatis.exceptions.TooManyResultsException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import static com.jfzt.meeting.constant.MessageConstant.EXCEPTION_TYPE;
import static com.jfzt.meeting.exception.ErrorCodeEnum.SERVICE_ERROR_A0400;
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
     * 处理io流异常
     */
    @ExceptionHandler(IOException.class)
    public Result<String> REIOException (IOException e) {
        log.error(e.getMessage(), e);
        Result<String> result = new Result<>();
        result.setMsg(e.getMessage());
        return result;
    }

    /**
     * 多结果异常
     */
    @ExceptionHandler(TooManyResultsException.class)
    public Result<String> TooManyRException (TooManyResultsException e) {
        log.error(SYSTEM_ERROR_B0001.getDescription() + EXCEPTION_TYPE, e.getMessage());
        return Result.fail(SYSTEM_ERROR_B0001);
    }

    /**
     * 空指针异常
     */
    @ExceptionHandler(NullPointerException.class)
    public Result<String> NullPointerException (NullPointerException e) {
        log.error(SERVICE_ERROR_A0400.getDescription() + EXCEPTION_TYPE, e.getMessage());
        return Result.fail(SERVICE_ERROR_A0400);
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
        result.setCode(e.getLocalizedMessage());
        result.setMsg(e.getMessage());
        return result;
    }

    /**
     * @param e 异常
     * @return {@code Result<String>}
     * @description 参数缺失
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Result<String> handleMissingServletRequestParameterException (
            MissingServletRequestParameterException e) {
        log.error("Exception occurred:", e);
        StackTraceElement[] stackTrace = e.getStackTrace();
        if (stackTrace.length > 0) {
            String exceptionLocation = stackTrace[0].toString();
            log.error("Exception occurred at: {}", exceptionLocation);
        }
        return Result.fail(ErrorCodeEnum.SERVICE_ERROR_A0410);
    }

    /**
     * 处理其他未知异常
     *
     * @return {@code Result<String>}
     */
    @ExceptionHandler(Exception.class)
    public Result<String> handleException (Exception e) {
        log.error("Exception occurred:", e);
        StackTraceElement[] stackTrace = e.getStackTrace();
        if (stackTrace.length > 0) {
            String exceptionLocation = stackTrace[0].toString();
            log.error("Exception occurred at: {}", exceptionLocation);
        }
        return Result.fail(ErrorCodeEnum.SYSTEM_ERROR_B0001);
    }
    /**
     * 调用企业微信接口异常
     */
    @ExceptionHandler(AesException.class)
    public Result<String> handleRRException(AesException e) {
        log.error(ErrorCodeEnum.SERVICE_ERROR_C0200.getCode(), ErrorCodeEnum.SERVICE_ERROR_C0200.getDescription());
        Result<String> result = new Result<>();
        result.setCode(ErrorCodeEnum.SERVICE_ERROR_C0200.getCode());
        result.setMsg(ErrorCodeEnum.SERVICE_ERROR_C0200.getDescription());

        return result;
    }

    @ExceptionHandler(value= MethodArgumentNotValidException.class)
    public Result<Map<String,String>> handleVaildException(MethodArgumentNotValidException e){
        log.error("数据校验出现问题{}，异常类型：{}",e.getMessage(),e.getClass());
        BindingResult bindingResult = e.getBindingResult();

        Map<String,String> errorMap = new HashMap<>();
        bindingResult.getFieldErrors().forEach((fieldError)->{
            errorMap.put(fieldError.getField(),fieldError.getDefaultMessage());
        });
        return Result.fail(ErrorCodeEnum.SERVICE_ERROR_A02011111,errorMap);
    }

}
