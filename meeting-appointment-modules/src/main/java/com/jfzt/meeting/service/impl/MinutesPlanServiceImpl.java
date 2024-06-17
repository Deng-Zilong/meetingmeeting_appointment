package com.jfzt.meeting.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jfzt.meeting.entity.MinutesPlan;
import com.jfzt.meeting.mapper.MinutesPlanMapper;
import com.jfzt.meeting.service.MinutesPlanService;
import org.springframework.stereotype.Service;

/**
 * 针对表【minutes_plan(迭代内容表)】的数据库操作Service实现
 * @author: chenyu.di
 * @since: 2024-06-17 15:23
 */
@Service
public class MinutesPlanServiceImpl extends ServiceImpl<MinutesPlanMapper, MinutesPlan> implements MinutesPlanService {

}