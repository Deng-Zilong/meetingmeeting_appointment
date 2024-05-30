package com.jfzt.meeting.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jfzt.meeting.entity.MeetingMinutes;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author zilong.deng
 * @description 针对表【meeting_minutes(会议纪要)】的数据库操作Mapper
 * @createDate 2024-05-29 17:59:44
 */
@Mapper
public interface MeetingMinutesMapper extends BaseMapper<MeetingMinutes> {

}




