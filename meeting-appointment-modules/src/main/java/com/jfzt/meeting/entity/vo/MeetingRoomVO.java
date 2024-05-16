package com.jfzt.meeting.entity.vo;

import lombok.Data;

/**
 * @author zilong.deng
 * @date 2024/04/28
 */

@Data
public class MeetingRoomVO {

    /**
     * 会议室id
     */
    private Long meetingRoomId;

    /**
     * 会议室名字
     */
    private String roomName;

    /**
     * 会议室状态
     */
    private Integer status;

}
