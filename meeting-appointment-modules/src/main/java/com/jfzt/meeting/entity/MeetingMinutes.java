package com.jfzt.meeting.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

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
     * 纪要id
     */
    @TableId(type = IdType.AUTO)
    private Long id;
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
    public boolean equals (Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MeetingMinutes that = (MeetingMinutes) o;
        return Objects.equals(id, that.id) && Objects.equals(userId, that.userId) && Objects.equals(minutes, that.minutes) && Objects.equals(remark, that.remark) && Objects.equals(meetingRecordId, that.meetingRecordId) && Objects.equals(gmtCreate, that.gmtCreate) && Objects.equals(gmtModified, that.gmtModified);
    }

    @Override
    public int hashCode () {
        return Objects.hash(id, userId, minutes, remark, meetingRecordId, gmtCreate, gmtModified);
    }

    @Override
    public String toString () {
        return "MeetingMinutes{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
                ", minutes='" + minutes + '\'' +
                ", remark='" + remark + '\'' +
                ", meetingRecordId=" + meetingRecordId +
                ", gmtCreate=" + gmtCreate +
                ", gmtModified=" + gmtModified +
                '}';
    }
}