package com.jfzt.meeting.entity.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jfzt.meeting.entity.DeviceErrorMessage;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author: chenyu.di
 * @since: 2024-06-12 16:09
 */
@Data
public class DeviceErrorMessageDTO {
    /**
     * 设备id
     */
    private Long deviceId;
    /**
     * 报损人
     */
    private String userId;
    /**
     * 报损信息
     */
    private String info;
}