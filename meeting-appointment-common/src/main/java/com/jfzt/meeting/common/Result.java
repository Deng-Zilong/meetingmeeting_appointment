package com.jfzt.meeting.common;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 返回状态定义
 *
 * @author zhenxing.lu
 * @since 2024-04-30 10.13:51
 */
@Data
public class Result<T> implements Serializable {
    private String code;
    private String msg;
    private T data;


    public static <T> Result<T> success (T data) {
        return new Result<>("200", "success", data);
    }

    public static <T> Result<T> success() {
        return new Result<>("200", "success", null);
    }

    public static <T> Result<T> success (String msg, T data) {
        return new Result<>("200", msg, data);
    }

    public static <T> Result<T> success (String msg) {
        return new Result<>("200", msg, null);
    }

    public static <T> Result<T> fail (String msg) {
        return new Result<>("500", msg, null);
    }

    public static <T> Result<T> fail (String code, String msg) {
        return new Result<>(code, msg, null);
    }


    public Result (String code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }


}
