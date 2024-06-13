package com.jfzt.meeting.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jfzt.meeting.entity.DeviceErrorMessage;
import org.apache.ibatis.annotations.Mapper;

/**
 * 针对表【device_error_message(报损表)】的数据库操作Mapper
 * @author: chenyu.di
 * @since: 2024-06-12 11:05
 */
@Mapper
public interface DeviceErrorMessageMapper extends BaseMapper<DeviceErrorMessage> {

}