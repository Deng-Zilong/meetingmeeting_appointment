package com.jfzt.meeting.entity.vo;

import lombok.Data;

import java.util.List;

/**
 * 会议室占用率VO
 *
 * @author zilong.deng
 * @since 2024-06-05 10:00:02
 */
@Data
public class MeetingRoomOccupancyVO {

    /**
     * 会议室id
     */
    private Long roomId;
    /**
     * 会议室名称
     */
    private String roomName;
    /**
     * 时间段总数
     */
    private Long total;

    /**
     * 总占用数
     */
    private Long totalOccupancy;

    /**
     * 总占用率
     */
    private Float totalOccupancyRate;

    /**
     * 不同时间段占用率（top1、top2、top3、other）
     */
    private List<TimePeriodOccupancyVO> timePeriods;

}
