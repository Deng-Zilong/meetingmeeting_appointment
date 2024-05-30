package com.jfzt.meeting.entity;

import lombok.Data;

/**
 * @author zilong.deng
 * @date 2024/05/30
 */
@Data
public class MeetingMinutesVO {
    /**
     *
     */
    private Integer id;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 纪要
     */
    private String minutes;

    /**
     * 会议记录id
     */
    private Integer meetingRecordId;

    /**
     * 会议记录主题
     */
    private String meetingRecordTitle;
}
