package com.jfzt.meeting.modules;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jfzt.meeting.common.Result;
import com.jfzt.meeting.entity.MeetingDevice;
import com.jfzt.meeting.exception.RRException;
import com.jfzt.meeting.mapper.MeetingDeviceMapper;
import com.jfzt.meeting.service.impl.MeetingDeviceServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;


import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class MeetingDeviceTest {


    @MockBean
    private MeetingDeviceMapper meetingDeviceMapper;



    @InjectMocks
    private MeetingDeviceServiceImpl meetingDeviceServiceImpl;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        MeetingDevice meetingDevice = new MeetingDevice();
        meetingDevice.setId(1L);
        meetingDevice.setStatus(1);

    }

    /**
     * 测试修改设备成功
     */
    @Test
    public void testUpdateDeviceSuccess() {
        // 准备测试数据
        MeetingDevice meetingDevice = MeetingDevice.builder()
                .roomId(1L)
                .deviceName("DeviceName")
                .extent(1)
                .status(1)
                .id(1L)
                .userId("userId")
                .gmtCreate(LocalDateTime.now())
                .gmtModified(LocalDateTime.now())
                .build();

        // 配置Mock行为：当查询满足条件的记录数的时候，返回0，表示设备名称不重复
        when(meetingDeviceMapper.selectCount(Mockito.<LambdaQueryWrapper<MeetingDevice>>any())).thenReturn(0L);

        // 调用待测试的方法
        Result<Object> result = meetingDeviceServiceImpl.updateDevice(meetingDevice);
        // 验证结果
        assertNotNull(result);
        assertEquals("修改成功!", result.getMsg());

        // 验证updateById是否被正确调用
        verify(meetingDeviceMapper, times(1)).updateById(eq(meetingDevice));
    }

    /**
     * 测试修改设备失败(设备名称重复)
     */
    @Test
    public void testUpdateDeviceFailed() {
        // 准备测试数据
        MeetingDevice meetingDevice = new MeetingDevice();
        meetingDevice.setRoomId(1L);
        meetingDevice.setDeviceName("ExistingDeviceName");

        // 配置Mock行为：当查询满足条件的记录数的时候返回大于0的结果，表示设备名称重复
        when(meetingDeviceMapper.selectCount(Mockito.<LambdaQueryWrapper<MeetingDevice>>any())).thenReturn(1L);
        // 调用待测试的方法
        Result<Object> result = meetingDeviceServiceImpl.updateDevice(meetingDevice);
        // 调用方法验证异常情况
        assertEquals("设备名重复!", result.getMsg());

        // 验证updateById是否被调用
        verify(meetingDeviceMapper, never()).updateById(any(MeetingDevice.class));
    }


    /**
     * 测试删除设备成功
     */
    @Test
    public void testDeleteDeviceSuccess() {
        Integer deviceId = 1;
        MeetingDevice existingDevice = new MeetingDevice();
        existingDevice.setId(1L);
        existingDevice.setStatus(1);

        when(meetingDeviceMapper.selectOne(any())).thenReturn(existingDevice);
        when(meetingDeviceMapper.deleteById(deviceId)).thenReturn(1);

        Result<Object> result = meetingDeviceServiceImpl.deleteDevice(deviceId);

        assertEquals("删除成功!", result.getMsg());
    }

    /**
     * 测试删除设备失败
     */
    @Test
    public void testDeleteDevice() {
        // 准备条件，设置一个会导致异常的设备ID
        Integer deviceId = -1;
        // 预期会抛出异常
        assertThrows(RRException.class, () -> meetingDeviceServiceImpl.deleteDevice(deviceId));
    }


}



