package com.jfzt.meeting.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 会议纪要(MeetingMinutes)实体类
 * @author zilong.deng
 * @since 2024-06-05 09:44:20
 */
@Data
public class MeetingMinutes implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    /**
     *纪要id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * 用户id
     */
    private String userId;
    /**
     * 纪要内容
     */
    private String minutes;
    /**
     * 纪要备注
     */
    private String remark;
    /**
     * 会议记录id
     */
    private Integer meetingRecordId;
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

    @Override
    public boolean equals (Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        MeetingMinutes other = (MeetingMinutes) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
                && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
                && (this.getMinutes() == null ? other.getMinutes() == null : this.getMinutes().equals(other.getMinutes()))
                && (this.getMeetingRecordId() == null ? other.getMeetingRecordId() == null : this.getMeetingRecordId().equals(other.getMeetingRecordId()));
    }

    @Override
    public int hashCode () {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getMinutes() == null) ? 0 : getMinutes().hashCode());
        result = prime * result + ((getMeetingRecordId() == null) ? 0 : getMeetingRecordId().hashCode());
        return result;
    }

    @Override
    public String toString () {
        String sb = getClass().getSimpleName() +
                " [" +
                "Hash = " + hashCode() +
                ", id=" + id +
                ", userId=" + userId +
                ", minutes=" + minutes +
                ", meetingRecordId=" + meetingRecordId +
                ", serialVersionUID=" + serialVersionUID +
                "]";
        return sb;
    }
}