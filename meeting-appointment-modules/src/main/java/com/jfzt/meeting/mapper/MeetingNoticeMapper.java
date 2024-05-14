package com.jfzt.meeting.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jfzt.meeting.entity.MeetingNotice;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author zilong.deng
 * @description 针对表【meeting_notice(会议公告表)】的数据库操作Mapper
 * @Entity generator.entity.MeetingNotice
 * @since 2024-04-28 11:45:32
 */
@Mapper
public interface MeetingNoticeMapper extends BaseMapper<MeetingNotice> {

}




