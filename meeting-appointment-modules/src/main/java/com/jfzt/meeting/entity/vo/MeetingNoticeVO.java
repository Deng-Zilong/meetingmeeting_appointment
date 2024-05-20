package com.jfzt.meeting.entity.vo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


/**
 * 新增公告VO对象
 *
 * @author xuchang.yang
 * @since: 2024-05-14 11:33
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MeetingNoticeVO implements Serializable {

    /**
     * 公告内容
     */
    private String substance;

    /**
     * 当前登录用户的权限等级
     */
    private Integer currentLevel;

    /**
     * 当前登录用户的id
     */
    private String currentUserId;

}
