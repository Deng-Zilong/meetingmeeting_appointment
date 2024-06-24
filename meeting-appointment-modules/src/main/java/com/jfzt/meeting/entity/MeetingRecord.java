package com.jfzt.meeting.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 会议记录表(MeetingRecord)表实体类
 * @author zilong.deng
 * @since 2024-06-05 09:43:39
 */
@TableName(value = "meeting_record")
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class MeetingRecord implements Serializable {
    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
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
     * 创建人姓名
     */
    private String adminUserName;
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
     * 0未删除 1删除
     */
    private Integer isDeleted;

    @Override
    public boolean equals (Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MeetingRecord that = (MeetingRecord) o;
        return Objects.equals(id, that.id)
                && Objects.equals(title, that.title)
                && Objects.equals(description, that.description)
                && Objects.equals(startTime, that.startTime)
                && Objects.equals(endTime, that.endTime)
                && Objects.equals(meetingRoomId, that.meetingRoomId)
                && Objects.equals(status, that.status)
                && Objects.equals(createdBy, that.createdBy)
                && Objects.equals(adminUserName, that.adminUserName)
                && Objects.equals(gmtCreate, that.gmtCreate)
                && Objects.equals(gmtModified, that.gmtModified)
                && Objects.equals(isDeleted, that.isDeleted);
    }

    @Override
    public int hashCode () {
        return Objects.hash(id, title, description, startTime, endTime, meetingRoomId, status, createdBy, adminUserName, gmtCreate, gmtModified, isDeleted);
    }

    @Override
    public String toString () {
        return "MeetingRecord{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", meetingRoomId=" + meetingRoomId +
                ", status=" + status +
                ", createdBy='" + createdBy + '\'' +
                ", adminUserName='" + adminUserName + '\'' +
                ", gmtCreate=" + gmtCreate +
                ", gmtModified=" + gmtModified +
                ", isDeleted=" + isDeleted +
                '}';
    }
}