package com.jfzt.meeting.entity.vo;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/** 返回用户信息
 * @author zhenxing.lu
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoVO  implements Serializable {
    private String accessToken;
    private String userId;
    private String name;
    private Integer level;
}
