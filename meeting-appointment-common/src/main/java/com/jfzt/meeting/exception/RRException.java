package com.jfzt.meeting.exception;

/**
 * 自定义异常
 *
 */
public class RRException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private String msg;
    private String code = "500";

    public RRException(String msg) {
        super(msg);
        this.msg = msg;
    }

    public RRException(String msg, Throwable e) {
        super(msg, e);
        this.msg = msg;
    }

    public RRException(String msg, String code) {
        super(msg);
        this.msg = msg;
        this.code = code;
    }

    public RRException(String msg, String code, Throwable e) {
        super(msg, e);
        this.msg = msg;
        this.code = code;
    }

    public RRException(ErrorCodeEnum errorCodeEnum) {
        super(errorCodeEnum.getDescription());
        this.msg = errorCodeEnum.getDescription();
        this.code = errorCodeEnum.getCode();
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(int String) {
        this.code = code;
    }


}
