package com.jfzt.meeting.exception;

import lombok.Getter;

/**
 * 错误码：
 * 1. 五位组成
 * 2. A代表用户端错误
 * 3. B代表当前系统异常
 * 4. C代表第三方服务异常
 * 4. 若无法确定具体错误，选择宏观错误
 * 6. 大的错误类间的步长间距预留100
 * @author zhenxing.lu
 * @since 2024-05-12 10:13:51
 */
@Getter
public enum ErrorCodeEnum {
    /**
     * 成功
     */
    SUCCESS("00000", "成功"),
    /**
     * 一级宏观错误码
     */
    USER_ERROR_0001("A0001", "用户端错误"),
    /**
     * 二级宏观错误码
     */
    USER_ERROR_A0100("A0100", "用户注册错误"),
    USER_ERROR_A0101("A0101", "用户未同意隐私协议"),
    SERVICE_ERROR_A0240("A0240", "用户验证码错误"),
    SERVICE_ERROR_A0201("A0201", "用户账户不存在"),
    SERVICE_ERROR_A02011111("A02011111", "参数格式校验失败"),
    SERVICE_ERROR_A020111("A020111", "用户不允许多方登录"),
    SERVICE_ERROR_A02011("A02011", "用户扫码登录失败请重新扫码"),
    SERVICE_ERROR_A0201111("A0201111", "企业微信登陆小程序失败"),
    SERVICE_ERROR_A0210("A0210", "用户密码错误"),
    SERVICE_ERROR_A0230("A0230", "用户登录已过期"),


    /**
     * 系统异常
     * 一级宏观错误码
     */
    SYSTEM_ERROR_B0001("B0001", "系统执行出错"),
    /**
     * 二级宏观错误码
     * (二) 异常处理
     * 1. 【强制】 catch 时请分清稳定代码和非稳定代码，稳定代码指的是无论如何不会出错的代码。对于非稳
     * 定代码的 catch 尽可能进行区分异常类型，再做对应的异常处理。
     * 2. 【强制】捕获异常是为了处理它，不要捕获了却什么都不处理而抛弃之，如果不想处理它，请将该异常
     * 抛给它的调用者。最外层的业务使用者，必须处理异常，将其转化为用户可以理解的内容。
     * 3. 【强制】事务场景中，抛出异常被 catch 后，如果需要回滚，一定要注意手动回滚事务。
     * 4. 【强制】finally 块必须对资源对象、流对象进行关闭，有异常也要做 try-catch。
     * 5. 【强制】不要在 finally 块中使用 return。
     */
    SYSTEM_ERROR_B0100("B0100", "系统执行超时"),
    SYSTEM_ERROR_B0101("B0101", "系统订单处理超时"),
    SYSTEM_ERROR_B01011("B01011", "用户导出失败，无模板信息"),
    SYSTEM_ERROR_B01012("B01012", "生成预览失败wordORexcel失败"),
    SYSTEM_ERROR_B01013("B01013", "用户生成预览失败，无模板信息"),

    /**
     * 调用第三方服务
     * 一级宏观错误码
     */
    SERVICE_ERROR_C0001("C0001", "调用第三方服务出错"),
    SERVICE_ERROR_C00011("C00011", "调用第三方服务出错url网页访问失败"),
    /**
     * 二级宏观错误码
     */
    SERVICE_ERROR_C0100("C0100", "中间件服务出错"),
    SERVICE_ERROR_C0110("C0110", "RPC 服务出错"),
    SERVICE_ERROR_C0111("C0200", "RPC 服务未找到"),
    SERVICE_ERROR_C0200("C0200", "第三方系统执行超时"),
    /**
     * 用户请求参数错误
     */
    SERVICE_ERROR_A0400("A0400", "用户请求参数错误"),
    SERVICE_ERROR_A0410("A0410", "请求必填参数为空"),
    SERVICE_ERROR_A0421("A0421", "参数格式不匹配"),


    /**
     * 用户访问权限异常
     */
    SERVICE_ERROR_A0301("A0301", "访问未授权"),
    /**
     * A0312 无权限使用 API
     */
    SERVICE_ERROR_A0312("A0312", "无权限使用 API");


    private final String code;
    private final String description;

    ErrorCodeEnum (String code, String description) {
        this.code = code;
        this.description = description;
    }

}