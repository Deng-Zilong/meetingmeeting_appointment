package com.jfzt.meeting.common;

import com.jfzt.meeting.exception.ErrorCodeEnum;
import lombok.Data;

import java.io.Serializable;


/**
 * 返回状态定义
 * @author zhenxing.lu
 * @since 2024-04-30 10:13:51
 */
@Data
public class Result<T> implements Serializable {
    private String code;
    private String msg;
    private T data;

    public Result () {
    }

    public static <T> Result<T> success (ErrorCodeEnum success) {
        return new Result<>(success.getCode(), success.getDescription(), null);
    }

    public static <T> Result<T> success (T data) {
        return new Result<>(ErrorCodeEnum.SUCCESS.getCode(), ErrorCodeEnum.SUCCESS.getDescription(), data);
    }

    public static <T> Result<T> fail (ErrorCodeEnum fails) {
        return new Result<>(fails.getCode(), fails.getDescription(), null);
    }
    public static <T> Result<T> fail (ErrorCodeEnum fails,T data) {
        return new Result<>(fails.getCode(), fails.getDescription(), data);
    }

    public static <T> Result<T> success () {
        return new Result<>(ErrorCodeEnum.SUCCESS.getCode(), ErrorCodeEnum.SUCCESS.getDescription(), null);
    }

    public static <T> Result<T> success (String msg, T data) {
        return new Result<>(ErrorCodeEnum.SUCCESS.getCode(), msg, data);
    }

    public static <T> Result<T> success (String msg) {
        return new Result<>(ErrorCodeEnum.SUCCESS.getCode(), msg, null);
    }

    public static <T> Result<T> fail (String msg) {
        return new Result<>(ErrorCodeEnum.SYSTEM_ERROR_B0001.getCode(), msg, null);
    }

    public Result (String code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }


}
