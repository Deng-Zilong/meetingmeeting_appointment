package com.jfzt.meeting.entity.vo;

import lombok.Data;

/**
 * 会议室占用率VO
 * @author zilong.deng
 * @since 2024-06-05 10:00:02
 */
@Data
public class MeetingRoomOccupancyVO {
    /**
     *时间段总数
     */
    private Long total;
    /**
     *    已占用数
     */
    private Long occupied;

    /**
     *占用率
     */
    private float occupancyRate;

    /**
     *会议室名称
     */
    private String name;
    /**
     *会议室id
     */
    private Long id;

}
