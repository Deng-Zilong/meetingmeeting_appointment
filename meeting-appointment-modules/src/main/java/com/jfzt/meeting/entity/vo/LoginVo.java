package com.jfzt.meeting.entity.vo;


import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;


/**
 * 登录Vo
 *
 * @author zhenxing.lu
 * @since: 2024-04-29 16:33
 */
@Data
public class LoginVo  implements Serializable {
    @NotNull(message = "用户名不能为空")
    private String name;
    @NotNull(message = "用户密码不能为空")
    private String password;
    @NotNull(message = "请重新获取验证码")
    private String uuid;
    @NotNull(message = "验证码不能为空")
    private String code;

}
