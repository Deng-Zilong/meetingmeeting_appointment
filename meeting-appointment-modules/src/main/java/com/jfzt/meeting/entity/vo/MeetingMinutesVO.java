package com.jfzt.meeting.entity.vo;

import lombok.Data;

import java.util.List;

/**
 * 会议纪要VO
 * @author zilong.deng
 * @since 2024-06-05 11:05:17
 */
@Data
public class MeetingMinutesVO {
    /**
     * id
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

    /**
     * 会议纪要迭代内容
     */
    private List<MinutesPlanVO> minutesPlans;
}
