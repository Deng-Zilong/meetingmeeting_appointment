package com.jfzt.meeting.entity.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Component;

/**
 * @author: chenyu.di
 * @since: 2024-06-12 14:39
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Component
public class MeetingDevicePageDTO extends PageDTO {
    /**
     * 会议室id
     */
    private String roomId;
    /**
     * 设备名称
     */
    private String deviceName;
    /**
     * 设备状态
     */
    private Integer status;

}