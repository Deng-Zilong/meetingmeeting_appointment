package com.jfzt.meeting.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 会议记录VO
 * @author zilong.deng
 * @since 2024-06-05 09:59:41
 */
@Data
public class MeetingRecordVO implements Serializable {

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
    private String location;

    /**
     * 参会人数
     */
    private Integer meetingNumber;
    /**
     * 参会人
     */
    private String attendees;

    /**
     * 参会人详细信息
     */
    private List<SysUserVO> users;

    /**
     * 部门名称
     */
    private String department;
    /**
     * 开始时间
     */
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;


    /**
     * 会议状态0已预约1进行中2已结束3已取消
     */
    private Integer status;

    /**
     * 0未删除 1删除
     */
    private Integer isDeleted;
}
