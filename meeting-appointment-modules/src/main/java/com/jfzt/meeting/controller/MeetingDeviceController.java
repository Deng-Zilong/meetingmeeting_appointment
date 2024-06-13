package com.jfzt.meeting.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jfzt.meeting.common.Result;
import com.jfzt.meeting.entity.DeviceErrorMessage;
import com.jfzt.meeting.entity.MeetingDevice;
import com.jfzt.meeting.entity.dto.DeviceErrorMessageDTO;
import com.jfzt.meeting.entity.dto.MeetingDeviceDTO;
import com.jfzt.meeting.entity.dto.MeetingDevicePageDTO;
import com.jfzt.meeting.entity.vo.MeetingDeviceVO;
import com.jfzt.meeting.service.DeviceErrorMessageService;
import com.jfzt.meeting.service.MeetingDeviceService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 设备管理控制类
 * @author: chenyu.di
 * @since: 2024-06-12 09:56
 */
@RestController
@RequestMapping("/meeting/meetingDevice")
public class MeetingDeviceController {
    @Resource
    private MeetingDeviceService meetingDeviceService;
    @Resource
    private DeviceErrorMessageService deviceErrorMessageService;
    /**
     * 获取设备信息
     * @param meetingDevicePageDTO 获取设备信息入参请求体
     * @return 设备信息
     */
    @GetMapping("/device")
    public Result<Page<MeetingDeviceVO>>getDevice (MeetingDevicePageDTO meetingDevicePageDTO) {
        return meetingDeviceService.getDevice(meetingDevicePageDTO);
    }
    /**
     * 添加设备
     * @param meetingDeviceDTO 添加设备入参请求体
     * @return 成功信息
     */
    @PostMapping("/device")
    public Result<Object>addDevice (@RequestBody MeetingDeviceDTO meetingDeviceDTO) {
        return meetingDeviceService.addDevice(meetingDeviceDTO);
    }
    /**
     * 修改设备
     * @param meetingDevice 修改设备入参请求体
     * @return 成功信息
     */
    @PutMapping("/device")
    public Result<Object>updateDevice (@RequestBody MeetingDevice meetingDevice) {
        return meetingDeviceService.updateDevice(meetingDevice);
    }
    /**
     * 启用禁用
     * @param id 设备id
     * @return 成功信息
     */
    @PutMapping("/statusDevice")
    public Result<Object>updateStatusDevice (@RequestParam Integer id) {
        return meetingDeviceService.updateStatusDevice(id);
    }
    /**
     * 删除设备
     * @param id 设备id
     * @return 成功信息
     */
    @DeleteMapping("/device")
    public Result<Object>deleteDevice (@RequestParam Integer id ) {
        return meetingDeviceService.deleteDevice(id);
    }

    /**
     * 获取报损信息
     * @param deviceId 设备id
     * @return 报损信息
     */
    @GetMapping("/info")
    public Result<List<DeviceErrorMessage>>getInfo (@RequestParam Integer deviceId) {
        return deviceErrorMessageService.getInfo(deviceId);
    }
    /**
     * 添加报损信息
     * @param deviceErrorMessageDTO 获取报损信息入参请求体
     * @return 成功信息
     */
    @PostMapping("/info")
    public Result<List<DeviceErrorMessage>>addInfo (@RequestBody DeviceErrorMessageDTO deviceErrorMessageDTO) {
        return deviceErrorMessageService.addInfo(deviceErrorMessageDTO);
    }
}