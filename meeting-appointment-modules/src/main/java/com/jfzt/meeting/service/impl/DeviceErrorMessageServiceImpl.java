package com.jfzt.meeting.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jfzt.meeting.common.Result;
import com.jfzt.meeting.entity.DeviceErrorMessage;
import com.jfzt.meeting.entity.MeetingDevice;
import com.jfzt.meeting.entity.MeetingRoom;
import com.jfzt.meeting.entity.SysUser;
import com.jfzt.meeting.entity.dto.DeviceErrorMessageDTO;
import com.jfzt.meeting.exception.ErrorCodeEnum;
import com.jfzt.meeting.exception.RRException;
import com.jfzt.meeting.mapper.DeviceErrorMessageMapper;
import com.jfzt.meeting.service.DeviceErrorMessageService;
import com.jfzt.meeting.service.MeetingDeviceService;
import com.jfzt.meeting.service.MeetingRoomService;
import com.jfzt.meeting.service.SysUserService;
import com.jfzt.meeting.utils.WxUtil;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 针对表【device_error_message(报损表)】的数据库操作Service实现
 * @author chenyu.di
 * @since 2024-06-12 11:08
 */
@Service
public class DeviceErrorMessageServiceImpl extends ServiceImpl<DeviceErrorMessageMapper, DeviceErrorMessage>
        implements DeviceErrorMessageService {
    @Resource
    private MeetingRoomService meetingRoomService;

    @Resource
    private WxUtil wxUtil;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private MeetingDeviceService meetingDeviceService;

    /**
     * 获取报损信息
     * @param deviceId 设备id
     * @return 报损信息
     */
    @Override
    public Result<List<DeviceErrorMessage>> getInfo (Long deviceId) {
        List<DeviceErrorMessage> list = lambdaQuery()
                .eq(DeviceErrorMessage::getDeviceId, deviceId)
                .list()
                .stream()
                .peek(deviceErrorMessage -> deviceErrorMessage.setUserName(sysUserService.lambdaQuery()
                        .eq(SysUser::getUserId, deviceErrorMessage.getUserId())
                        .one()
                        .getUserName()))
                .toList();
        return Result.success(list);
    }

    /**
     * 添加报损信息
     * @param deviceErrorMessageDTO 获取报损信息入参请求体
     * @return 成功信息
     */
    @Override
    public Result<List<DeviceErrorMessage>> addInfo (DeviceErrorMessageDTO deviceErrorMessageDTO) {
        if (deviceErrorMessageDTO.getInfo() == null) {
            throw new RRException(ErrorCodeEnum.SERVICE_ERROR_A0410);
        }
        DeviceErrorMessage deviceErrorMessage = new DeviceErrorMessage();
        BeanUtils.copyProperties(deviceErrorMessageDTO, deviceErrorMessage);
        this.save(deviceErrorMessage);

        // 更新设备报损次数
        MeetingDevice meetingDevice = meetingDeviceService.lambdaQuery()
                .eq(MeetingDevice::getId, deviceErrorMessageDTO.getDeviceId())
                .one();
        Integer extent = meetingDevice.getExtent();
        extent++;
        meetingDevice.setExtent(extent);
        meetingDeviceService.lambdaUpdate()
                .eq(MeetingDevice::getId, deviceErrorMessageDTO.getDeviceId())
                .set(MeetingDevice::getExtent, extent)
                .update();
        MeetingRoom room = meetingRoomService.lambdaQuery()
                .eq(MeetingRoom::getId, meetingDevice.getRoomId())
                .one();
        try {
            // 判断报损次数是否大于等于3,停止报损是否开启,0可以报损
            if (meetingDevice.getStopSend() == 0) {

                if (extent >= 3) {
                    meetingDevice.setStatus(0);
                    wxUtil.sendsWxReminders(sysUserService.lambdaQuery()
                            .ne(SysUser::getLevel, 2)
                            .list()
                            .stream()
                            .map(SysUser::getUserId)
                            .toList(), room.getRoomName()
                            + "的"
                            + meetingDevice.getDeviceName()
                            + "\n因"
                            + deviceErrorMessageDTO.getInfo()
                            + "\n目前已被禁用，请及时处理!!");
                } else {
                    wxUtil.sendsWxReminders(sysUserService.lambdaQuery()
                            .ne(SysUser::getLevel, 2)
                            .list()
                            .stream()
                            .map(SysUser::getUserId)
                            .toList(), room.getRoomName()
                            + "的"
                            + meetingDevice.getDeviceName()
                            + "已损坏\n损坏原因为："
                            + deviceErrorMessageDTO.getInfo()
                            + "\n请及时处理!!");
                }

            }
        } catch (Exception e) {
            throw new RRException(ErrorCodeEnum.SERVICE_ERROR_A0400);
        }
        //每十次发送一次报损信息
        if (extent % 10 == 0) {

            wxUtil.sendsWxReminders(sysUserService.lambdaQuery()
                            .ne(SysUser::getLevel, 2)
                            .list()
                            .stream()
                            .map(SysUser::getUserId)
                            .toList()
                    , "【"
                            + room.getRoomName()
                            + "】的【"
                            + meetingDevice.getDeviceName()
                            + "】已申请 【"
                            + meetingDevice.getExtent()
                            + "次】报损"
                            + "请及时处理!!");
            return Result.fail("【"
                    + room.getRoomName()
                    + "】的【"
                    + meetingDevice.getDeviceName()
                    + "】已申请【"
                    + meetingDevice.getExtent()
                    + "次】报损,请及时处理!!");
        }

        return Result.success("提交成功");
    }
}
