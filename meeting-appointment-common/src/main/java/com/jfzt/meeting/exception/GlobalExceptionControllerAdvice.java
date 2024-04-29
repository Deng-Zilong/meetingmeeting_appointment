package com.jfzt.meeting.exception;

import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 集中处理所有异常
 */
@RestControllerAdvice(basePackages = "com.iam.roomappointment.controller")
public class GlobalExceptionControllerAdvice {


    //    @ExceptionHandler(value= MethodArgumentNotValidException.class)
    //    public R handleVaildException(MethodArgumentNotValidException e){
    //        log.error("数据校验出现问题{}，异常类型：{}",e.getMessage(),e.getClass());
    //        BindingResult bindingResult = e.getBindingResult();
    //
    //        Map<String,String> errorMap = new HashMap<>();
    //        bindingResult.getFieldErrors().forEach((fieldError)->{
    //            errorMap.put(fieldError.getField(),fieldError.getDefaultMessage());
    //        });
    //        return R.error(ErrorCodeEnum.VAILD_EXCEPTION.getCode(), ErrorCodeEnum.VAILD_EXCEPTION.getMsg()).put("data",errorMap);
    //    }
    //
    //    @ExceptionHandler(value = Throwable.class)
    //    public R handleException(Throwable throwable){
    //
    //        log.error("错误：",throwable);
    //        return R.error(ErrorCodeEnume.UNKNOW_EXCEPTION.getCode(), ErrorCodeEnume.UNKNOW_EXCEPTION.getMsg());
    //    }


}
