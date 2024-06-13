package com.jfzt.meeting.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jfzt.meeting.common.Result;
import com.jfzt.meeting.entity.DeviceErrorMessage;
import com.jfzt.meeting.entity.MeetingDevice;
import com.jfzt.meeting.entity.dto.DeviceErrorMessageDTO;
import com.jfzt.meeting.exception.ErrorCodeEnum;
import com.jfzt.meeting.exception.RRException;
import com.jfzt.meeting.mapper.DeviceErrorMessageMapper;
import com.jfzt.meeting.service.DeviceErrorMessageService;
import com.jfzt.meeting.service.MeetingDeviceService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 针对表【device_error_message(报损表)】的数据库操作Service实现
 * @author: chenyu.di
 * @since: 2024-06-12 11:08
 */
@Service
public class DeviceErrorMessageServiceImpl extends ServiceImpl<DeviceErrorMessageMapper,DeviceErrorMessage>
        implements DeviceErrorMessageService {

    @Resource
    private MeetingDeviceService meetingDeviceService;

    /**
     * 获取报损信息
     * @param deviceId 设备id
     * @return 报损信息
     */
    @Override
    public Result<List<DeviceErrorMessage>> getInfo(Integer deviceId) {
        List<DeviceErrorMessage> list = lambdaQuery().eq(DeviceErrorMessage::getDeviceId, deviceId).list();
        return Result.success(list);
    }

    /**
     * 添加报损信息
     * @param deviceErrorMessageDTO 获取报损信息入参请求体
     * @return 成功信息
     */
    @Override
    public Result<List<DeviceErrorMessage>> addInfo(DeviceErrorMessageDTO deviceErrorMessageDTO) {
        if (deviceErrorMessageDTO.getInfo() == null){
            throw new RRException(ErrorCodeEnum.SERVICE_ERROR_A0410);
        }
        DeviceErrorMessage deviceErrorMessage = new DeviceErrorMessage();
        BeanUtils.copyProperties(deviceErrorMessageDTO,deviceErrorMessage);
        this.save(deviceErrorMessage);

        // 更新设备报损次数
        MeetingDevice meetingDevice = meetingDeviceService.lambdaQuery()
                .eq(MeetingDevice::getId, deviceErrorMessageDTO.getDeviceId())
                .one();
        Integer extent = meetingDevice.getExtent();
        extent++;
        meetingDevice.setExtent(extent);
        meetingDeviceService.updateById(meetingDevice);

        return Result.success("提交成功");
    }
}
