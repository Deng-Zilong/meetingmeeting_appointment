package com.jfzt.meeting.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jfzt.meeting.entity.MinutesPlan;
import org.apache.ibatis.annotations.Mapper;

/**
 * 针对表【minutes_plan(迭代内容表)】的数据库操作Mapper
 * @author chenyu.di
 * @since  2024-06-17 11:50:45
 */
@Mapper
public interface MinutesPlanMapper extends BaseMapper<MinutesPlan> {

}
