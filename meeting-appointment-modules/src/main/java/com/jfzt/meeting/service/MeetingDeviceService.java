package com.jfzt.meeting.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jfzt.meeting.common.Result;
import com.jfzt.meeting.entity.MeetingDevice;
import com.jfzt.meeting.entity.dto.MeetingDeviceDTO;
import com.jfzt.meeting.entity.dto.MeetingDevicePageDTO;
import com.jfzt.meeting.entity.vo.MeetingDeviceVO;
import com.jfzt.meeting.entity.vo.PageVO;

import java.util.List;

/**
 *  针对表【meeting_device(设备表)】的数据库操作Service
 * @author: chenyu.di
 * @since: 2024-06-12 11:09
 */
public interface MeetingDeviceService extends IService<MeetingDevice> {

    /**
     * 获取设备信息
     * @param meetingDevicePageDTO 获取设备信息入参请求体
     * @return 设备信息
     */
    Result<Page<MeetingDeviceVO>> getDevice(MeetingDevicePageDTO meetingDevicePageDTO);

    /**
     * 添加设备
     * @param meetingDeviceDTO 添加设备入参请求体
     * @return 成功信息
     */
    Result<Object> addDevice(MeetingDeviceDTO meetingDeviceDTO);

    /**
     * 修改设备
     * @param meetingDevice 修改设备入参请求体
     * @return 成功信息
     */
    Result<Object> updateDevice(MeetingDevice meetingDevice);

    /**
     * 删除设备
     * @param id 设备id
     * @return 成功信息
     */
    Result<Object> deleteDevice(Integer id);

    /**
     * 启用禁用
     * @param id 设备id
     * @return 成功信息
     */
    Result<Object> updateStatusDevice(Integer id);
}
