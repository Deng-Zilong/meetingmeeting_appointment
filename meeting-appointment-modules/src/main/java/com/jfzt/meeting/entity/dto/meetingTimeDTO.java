package com.jfzt.meeting.entity.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 会议时间dto
 * @author zilong.deng
 * @since 2024/06/04
 */
@Data
public class meetingTimeDTO {


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
     * 用户id
     */
    private String userId;
}
