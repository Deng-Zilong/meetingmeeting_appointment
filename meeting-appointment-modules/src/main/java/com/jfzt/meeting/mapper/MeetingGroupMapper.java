package com.jfzt.meeting.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jfzt.meeting.entity.MeetingGroup;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author zilong.deng
 * @description 针对表【meeting_group(群组表)】的数据库操作Mapper
 * @Entity com.jfzt.meeting.entity.MeetingGroup
 * @since 2024-04-28 11:33:49
 */
@Mapper
public interface MeetingGroupMapper extends BaseMapper<MeetingGroup> {

}




