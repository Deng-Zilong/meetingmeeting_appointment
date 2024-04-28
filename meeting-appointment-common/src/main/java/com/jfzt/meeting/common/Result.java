package com.jfzt.meeting.common;

import lombok.Data;

/**
 * @author zilong.deng
 */
@Data
public class Result<T> {
    private String code;
    private String msg;
    private T data;


    public static <T> Result<T> success (T data) {
        return new Result<>("200", "success", data);
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
