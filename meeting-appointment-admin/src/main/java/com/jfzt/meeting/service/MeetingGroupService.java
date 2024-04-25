package com.jfzt.meeting.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jfzt.meeting.entity.MeetingGroupEntity;

import java.util.List;


/**
 * @author zhenxing.lu
 * @sine 2024-04-24 11:03:26
 */
public interface MeetingGroupService extends IService<MeetingGroupEntity> {

    List<MeetingGroupEntity> find ();
}

