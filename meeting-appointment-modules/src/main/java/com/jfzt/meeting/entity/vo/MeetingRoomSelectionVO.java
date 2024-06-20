package com.jfzt.meeting.entity.vo;

import lombok.Data;

/**
 * 会议室选择率VO
 * @author zilong.deng
 * @since 2024-06-17 14:42:49
 */
@Data
public class MeetingRoomSelectionVO {

    /**
     * 会议室id
     */
    private Long roomId;
    /**
     * 会议室名称
     */
    private String roomName;

    /**
     * 会议总次数
     */
    private Long total;
    /**
     * 被选择次数
     */
    private Long selected;


}
