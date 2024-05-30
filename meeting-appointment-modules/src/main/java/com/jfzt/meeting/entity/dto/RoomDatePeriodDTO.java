package com.jfzt.meeting.entity.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author zilong.deng
 * @date 2024/05/24
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
