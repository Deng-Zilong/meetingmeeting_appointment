package com.jfzt.meeting.entity.vo;


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

    private String name;
    private String password;
    private String uuid;
    private String code;

}
