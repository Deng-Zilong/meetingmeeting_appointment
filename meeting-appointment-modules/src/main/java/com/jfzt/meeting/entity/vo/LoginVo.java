package com.jfzt.meeting.entity.vo;


import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;


/**
 * 登录Vo
 * @author zhenxing.lu
 * @since 2024-06-05 09:45:57
 */
@Data
public class LoginVo  implements Serializable {
    /**
     * 用户名
     */
    @NotNull(message = "用户名不能为空")
    private String name;
    /**
     * 密码
     */
    @NotNull(message = "用户密码不能为空")
    private String password;
    /**
     * uuid
     */
    @NotNull(message = "请重新获取验证码")
    private String uuid;
    /**
     * 验证码
     */
    @NotNull(message = "验证码不能为空")
    private String code;

}
