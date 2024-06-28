package com.jfzt.meeting.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jfzt.meeting.common.Result;
import com.jfzt.meeting.entity.DeviceErrorMessage;
import com.jfzt.meeting.entity.dto.DeviceErrorMessageDTO;

import java.util.List;

/**
 * 针对表【device_error_message(报损表)】的数据库操作Service
 * @author: chenyu.di
 * @since: 2024-06-12 11:09
 */
public interface DeviceErrorMessageService extends IService<DeviceErrorMessage> {

    /**
     * 获取报损信息
     * @param deviceId 设备id
     * @return 报损信息
     */
    Result<List<DeviceErrorMessage>> getInfo (Long deviceId);

    /**
     * 添加报损信息
     * @param deviceErrorMessageDTO 获取报损信息入参请求体
     * @return 成功信息
     */
    Result<List<DeviceErrorMessage>> addInfo (DeviceErrorMessageDTO deviceErrorMessageDTO);
}
