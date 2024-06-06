package com.jfzt.meeting.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 修改会议室状态DTO
 * @author xuchang.yang
 * @since 2024-05-15 10:50
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MeetingRoomDTO {
    /**
     * 会议室id
     */
    Long id;
    /**
     * 会议室状态
     */
    Integer status;
    /**
     * 当前登录用户权限
     */
    Integer currentLevel;
}
