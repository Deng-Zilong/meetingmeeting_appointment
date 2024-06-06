package com.jfzt.meeting.entity.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jfzt.meeting.entity.SysUser;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 会议记录DTO
 * @author chenyu.di
 * @since 2024-05-09 09:51
 */

@Data
public class MeetingRecordDTO {
    /**
     * 会议id
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
     * 开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    /**
     * 会议室id
     */
    private Long meetingRoomId;

    /**
     * 会议状态0已预约1进行中2已结束3已取消
     */
    private Integer status;

    /**
     * 创建人id
     */
    private String createdBy;

    /**
     * 添加时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime gmtCreate;

    /**
     * 修改时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime gmtModified;

    /**
     * 参会人员
     */
    private List<SysUser> users = new ArrayList<>();

}