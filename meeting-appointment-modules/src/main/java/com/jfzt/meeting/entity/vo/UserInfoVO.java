package com.jfzt.meeting.entity.vo;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 返回用户信息VO
 * @author zhenxing.lu
 * @since 2024-06-05 10:02:42
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoVO  implements Serializable {

    /**
     * accessToken
     */
    private String accessToken;

    /**
     * 用户id
     */
    private String userId;
    /**
     * 姓名
     */
    private String name;
    /**
     * 等级
     */
    private Integer level;
    /**
     * url
     */
    private String url;
}
