package com.jfzt.meeting.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 时间段状态VO
 * @author zilong.deng
 * @since 2024-06-05 10:02:33
 */

@Data
public class TimePeriodStatusVO {
    /**
     * 时间段开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    LocalDateTime startTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    LocalDateTime endTime;
    /**
     * 时间段占用会议创建人
     */
    String meetingAdminUserName;

    String meetingTitle;
    /**
     * 时间段状态
     */
    Integer status;
}
