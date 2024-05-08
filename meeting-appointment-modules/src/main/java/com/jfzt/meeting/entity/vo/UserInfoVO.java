package com.jfzt.meeting.entity.vo;


import lombok.Data;

import java.io.Serializable;

/** 返回用户信息
 * @author zhenxing.lu
 */
@Data
public class UserInfoVO  implements Serializable {
    private String accessToken;
    private String userId;
    private String name;
    private Integer level;
}
