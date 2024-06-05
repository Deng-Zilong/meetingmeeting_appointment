package com.jfzt.meeting.entity.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 会议室日期段DTO
 * @author zilong.deng
 * @since 2024-06-05 10:05:14
 */
@Data
public class RoomDatePeriodDTO {

    /**
     * 开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    LocalDateTime startDate;

    /**
     * 结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    LocalDateTime endDate;

    /**
     * 会议室id
     */
    Long roomId;

}
