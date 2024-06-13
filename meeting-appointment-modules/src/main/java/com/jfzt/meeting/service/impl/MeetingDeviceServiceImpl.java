package com.jfzt.meeting.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jfzt.meeting.common.Result;
import com.jfzt.meeting.entity.MeetingDevice;
import com.jfzt.meeting.entity.MeetingRoom;
import com.jfzt.meeting.entity.dto.MeetingDeviceDTO;
import com.jfzt.meeting.entity.dto.MeetingDevicePageDTO;
import com.jfzt.meeting.entity.vo.MeetingDeviceVO;
import com.jfzt.meeting.exception.ErrorCodeEnum;
import com.jfzt.meeting.exception.RRException;
import com.jfzt.meeting.mapper.MeetingDeviceMapper;
import com.jfzt.meeting.service.MeetingDeviceService;
import com.jfzt.meeting.service.MeetingRoomService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
/**
 * 针对表【meeting_device(设备表)】的数据库操作Service实现
 * @author: chenyu.di
 * @since: 2024-06-12 11:08
 */
@Service
public class MeetingDeviceServiceImpl extends ServiceImpl<MeetingDeviceMapper,MeetingDevice>
        implements MeetingDeviceService {
    @Resource
    private MeetingRoomService meetingRoomService;
    /**
     * 获取设备信息
     * @param meetingDevicePageDTO 获取设备信息入参请求体
     * @return 设备信息
     */
    @Override
    public Result<Page<MeetingDeviceVO>> getDevice(MeetingDevicePageDTO meetingDevicePageDTO) {
        try{
            Page<MeetingDevice> page = this.page(new Page<>(meetingDevicePageDTO.getCurrent(),
                    meetingDevicePageDTO.getSize()), new LambdaQueryWrapper<MeetingDevice>()
                    .eq(meetingDevicePageDTO.getRoomId() != null,
                            MeetingDevice::getRoomId, meetingDevicePageDTO.getRoomId())
                    .eq(meetingDevicePageDTO.getStatus() != null,
                            MeetingDevice::getStatus, meetingDevicePageDTO.getStatus())
                    .like(meetingDevicePageDTO.getDeviceName() != null,
                            MeetingDevice::getDeviceName, meetingDevicePageDTO.getDeviceName()));
            Page<MeetingDeviceVO> deviceVOPage = new Page<>();
             deviceVOPage.setRecords(page.getRecords().stream().map(meetingDevice -> {
                MeetingDeviceVO meetingDeviceVO = new MeetingDeviceVO();
                BeanUtils.copyProperties(meetingDevice, meetingDeviceVO);
                String roomName = meetingRoomService.lambdaQuery()
                        .eq(MeetingRoom::getId, meetingDevice.getRoomId())
                        .one()
                        .getRoomName();
                meetingDeviceVO.setRoomName(roomName);
                return meetingDeviceVO;
            }).toList());
            BeanUtils.copyProperties(page, deviceVOPage,"records");
            return Result.success(deviceVOPage);
        }catch (Exception e){
            throw new RRException(ErrorCodeEnum.SERVICE_ERROR_A0400);
        }
    }

    /**
     * 添加设备
     * @param meetingDeviceDTO 添加设备入参请求体
     * @return 成功信息
     */
    @Override
    public Result<Object> addDevice(MeetingDeviceDTO meetingDeviceDTO) {
        if (meetingDeviceDTO.getRoomId()==null || meetingDeviceDTO.getDeviceName()==null || meetingDeviceDTO.getUserId()==null){
            throw new RRException(ErrorCodeEnum.SERVICE_ERROR_A0410);
        }
        MeetingDevice meetingDevice = new MeetingDevice();
        BeanUtils.copyProperties(meetingDeviceDTO, meetingDevice);
        Long count = lambdaQuery().eq(MeetingDevice::getRoomId, meetingDeviceDTO.getRoomId())
                .eq(MeetingDevice::getDeviceName, meetingDeviceDTO.getDeviceName())
                .count();
        if (count>0){
            return Result.fail("设备名重复!");
        }
        this.save(meetingDevice);
        return Result.success("添加成功!");
    }

    /**
     * 修改设备
     * @param meetingDevice 修改设备入参请求体
     * @return 成功信息
     */
    @Override
    public Result<Object> updateDevice(MeetingDevice meetingDevice) {
        try{
            this.updateById(meetingDevice);
            return Result.success("修改成功!");
        }catch (Exception e){
            throw new RRException(ErrorCodeEnum.SERVICE_ERROR_A0400);
        }
    }

    /**
     * 删除设备
     * @param id 设备id
     * @return 成功信息
     */
    @Override
    public Result<Object> deleteDevice(Integer id) {
        try {
            this.removeById(id);
            return Result.success("删除成功!");
        }catch (Exception e){
            throw new RRException(ErrorCodeEnum.SERVICE_ERROR_A0400);
        }
    }

    /**
     * 启用禁用
     * @param id 设备id
     * @return 成功信息
     */
    @Override
    public Result<Object> updateStatusDevice(Integer id) {
        try {
            MeetingDevice meetingDevice = this.lambdaQuery().eq(MeetingDevice::getId, id).one();
            meetingDevice.setStatus(meetingDevice.getStatus()==1 ? 0 : 1);
            if (meetingDevice.getStatus() == 1){
                meetingDevice.setExtent(0);
            }
            this.updateById(meetingDevice);
            return Result.success();
        }catch (Exception e){
            throw new RRException(ErrorCodeEnum.SERVICE_ERROR_A0400);
        }
    }
}
