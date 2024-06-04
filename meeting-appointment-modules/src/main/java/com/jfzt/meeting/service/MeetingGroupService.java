package com.jfzt.meeting.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jfzt.meeting.common.Result;
import com.jfzt.meeting.entity.MeetingGroup;
import com.jfzt.meeting.entity.dto.MeetingGroupDTO;
import com.jfzt.meeting.entity.vo.MeetingGroupVO;

import java.util.List;

/**
 * @author zilong.deng
 * @description 针对表【meeting_group(群组表)】的数据库操作Service
 * @since 2024-04-28 11:33:49
 */
public interface MeetingGroupService extends IService<MeetingGroup> {


    /**
     * 群组查询
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @param userId 用户id
     * @return 群组列表
     */
    Result<List<MeetingGroupVO>> checkGroup (Integer pageNum, Integer pageSize, String userId);

    /**
     * 群组添加
     * @param meetingGroupDTO 群组DTO
     * @return 添加结果
     */
    Result<Object> addMeetingGroup (MeetingGroupDTO meetingGroupDTO);

    /**
     * 群组修改
     * @param meetingGroupDTO 群组DTO
     * @return 修改结果
     */
    Result<Object> updateMeetingGroup (MeetingGroupDTO meetingGroupDTO);

    /**
     * 群组删除
     * @param id 群组id
     * @return 删除结果
     */
    Result<Object> deleteMeetingGroup (Long id);
}
