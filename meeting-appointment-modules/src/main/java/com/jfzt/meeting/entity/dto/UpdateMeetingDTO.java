package com.jfzt.meeting.entity.dto;

import lombok.Data;

/**
 * 更新会议DTO
 * @author zilong.deng
 * @since 2024-06-05 10:04:44
 */
@Data
public class UpdateMeetingDTO {

    /**
     * 用户id
     */
    private String userId;

    /**
     * 会议id
     */
    private Long meetingId;
}
