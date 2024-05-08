package com.jfzt.meeting.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author zilong.deng
 * @date 2024/04/29
 */
@Data
public class MeetingRecordVO {

    /**
     * 会议记录id
     */
    private Long id;

    /**
     * 会议主题
     */
    private String title;

    /**
     * 会议概述
     */
    private String description;

    /**
     * 创建人id
     */
    private String createdBy;
    /**
     * 创建人姓名
     */
    private String adminUserName;

    /**
     * 会议室id
     */
    private Long meetingRoomId;
    /**
     * 会议室名称
     */
    private String meetingRoomName;

    /**
     * 会议室地点
     */
    private Integer location;

    /**
     * 参会人数
     */
    private Integer meetingNumber;
    /**
     * 参会人
     */
    private String attendees;

    /**
     * 开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    /**
     * 会议状态0未开始2进行中3已结束
     */
    private Integer status;

    /**
     * 0未删除 1删除
     */
    private Integer isDeleted;
}
