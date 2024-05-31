package com.jfzt.meeting.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author zilong.deng
 * @description 会议室状态VO
 * @date 2024/05/06
 */
@Data
public class MeetingRoomStatusVO {


    /**
     * 会议室id
     */
    private Long id;
    /**
     * 会议室名称
     */
    private String roomName;

    /**
     * 会议室位置
     */
    private String location;
    /**
     * 会议室状态（0暂停使用,1空闲，2使用中）
     */
    private Integer status;

    /**
     * 容量
     */
    private Integer capacity;

    /**
     * 会议室设备
     */
    private String equipment;
    /**
     * 会议主题
     */
    private String title;
    /**
     * 会议描述
     */
    private String description;
    /**
     * 参会人
     */
    private String attendees;
    /**
     * 会议开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime meetingStartTime;
    /**
     * 会议结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime meetingEndTime;
    /**
     * 会议状态(0已预约1进行中2已结束3已取消)
     */
    private Integer recordStatus;

}
