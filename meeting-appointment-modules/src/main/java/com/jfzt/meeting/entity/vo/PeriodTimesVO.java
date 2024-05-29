package com.jfzt.meeting.entity.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Author: chenyu.di
 * @since: 2024-05-28 16:14
 */
@Data
@Builder
public class PeriodTimesVO {
    /**
     * 时间段总数
     */
    Long count;
    /**
     * 时间段开始时间
     */
    LocalDateTime startTime;
    /**
     * 时间段结束时间
     */
    LocalDateTime endTime;

}