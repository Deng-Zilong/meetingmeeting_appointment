package com.jfzt.meeting.entity.vo;

import lombok.Data;

/**
 * 会议室VO
 * @author zilong.deng
 * @since 2024-06-05 10:01:45
 */

@Data
public class MeetingRoomVO {

    /**
     * 会议室id
     */
    private Long id;

    /**
     * 会议室名字
     */
    private String roomName;

    /**
     * 会议室状态
     */
    private Integer status;

}
