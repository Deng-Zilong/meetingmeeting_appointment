package com.jfzt.meeting.exception;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

/**
 * 自定义异常
 */
@EqualsAndHashCode(callSuper = true)
@Getter
public class RRException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    @Setter
    private String msg;
    @Setter
    private String code;


    public RRException (String msg) {
        super(msg);
        this.msg = msg;
    }

    public RRException (String msg, Throwable e) {
        super(msg, e);
        this.msg = msg;
    }

    public RRException (String msg, String code) {
        super(msg);
        this.msg = msg;
        this.code = code;
    }

    public RRException (String msg, String code, Throwable e) {
        super(msg, e);
        this.msg = msg;
        this.code = code;
    }

    public RRException (ErrorCodeEnum errorCodeEnum) {
        super(errorCodeEnum.getDescription());
        this.msg = errorCodeEnum.getDescription();
        this.code = errorCodeEnum.getCode();
    }


}
