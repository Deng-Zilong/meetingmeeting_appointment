package com.jfzt.meeting.entity.vo;

import lombok.Data;

/**
 * 时间段占用情况VO
 * @author zilong.deng
 * @since 2024-06-17 15:11:13
 */
@Data
public class TimePeriodOccupancyVO {
    /**
     * 时间段信息
     */
    private String timePeriod;

    /**
     * 被占用次数
     */
    private Long occupied;

    /**
     * 占用率
     */
    private float occupancyRate;

}
