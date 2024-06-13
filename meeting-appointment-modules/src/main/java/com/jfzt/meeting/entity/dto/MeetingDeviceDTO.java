package com.jfzt.meeting.entity.dto;

import lombok.Data;

/**
 * @author: chenyu.di
 * @since: 2024-06-12 16:29
 */
@Data
public class MeetingDeviceDTO {
    /**
     * 会议室id
     */
    private Long roomId;
    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 创建人id
     */
    private String userId;
}