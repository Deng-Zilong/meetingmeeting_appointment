package com.jfzt.meeting.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jfzt.meeting.common.Result;
import com.jfzt.meeting.constant.IsDeletedConstant;
import com.jfzt.meeting.constant.MessageConstant;
import com.jfzt.meeting.entity.MeetingNotice;
import com.jfzt.meeting.entity.vo.MeetingNoticeVO;
import com.jfzt.meeting.exception.ErrorCodeEnum;
import com.jfzt.meeting.exception.RRException;
import com.jfzt.meeting.mapper.MeetingNoticeMapper;
import com.jfzt.meeting.service.MeetingNoticeService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 针对表【meeting_notice(会议公告表)】的数据库操作Service实现
 * @author xuchang.yang
 * @since 2024-04-28 11:45:33
 */
@Service
public class MeetingNoticeServiceImpl extends ServiceImpl<MeetingNoticeMapper, MeetingNotice>
        implements MeetingNoticeService {

    @Resource
    private MeetingNoticeMapper meetingNoticeMapper;


    /**
     * 上传公告
     * @param meetingNoticeVO 公告信息VO对象
     * @return Result<Integer> 返回结果
     */
    @Override
    public Result<Integer> addNotice(MeetingNoticeVO meetingNoticeVO) {
        // 判断当前用户是否是管理员或超级管理员
        if (MessageConstant.SUPER_ADMIN_LEVEL.equals(meetingNoticeVO.getCurrentLevel()) ||
                MessageConstant.ADMIN_LEVEL.equals(meetingNoticeVO.getCurrentLevel())){
            if (meetingNoticeVO.getCurrentUserId().isEmpty() || meetingNoticeVO.getSubstance().isEmpty() ||
                    meetingNoticeVO.getCurrentLevel() == null){
                throw new RRException(ErrorCodeEnum.SERVICE_ERROR_A0400);
            }
            MeetingNotice meetingNotice = new MeetingNotice();
            meetingNotice.setUserId(meetingNoticeVO.getCurrentUserId());
            meetingNotice.setSubstance(meetingNoticeVO.getSubstance());
            int insert = meetingNoticeMapper.insert(meetingNotice);
            if (insert > 0){
                return Result.success(insert);
            } else {
                return Result.fail(ErrorCodeEnum.SYSTEM_ERROR_B0001);
            }
        } else {
            throw new RRException(ErrorCodeEnum.SERVICE_ERROR_A0301);
        }
    }

    /**
     * 查询所有通告信息
     * @return 通告信息
     */
    @Override
    public Result<List<String>> selectAll() {
        List<MeetingNotice> meetingNotices = meetingNoticeMapper.selectList(new QueryWrapper<>());
        return Result.success(meetingNotices.stream()
                .filter(notice -> IsDeletedConstant.NOT_DELETED.equals(notice.getIsDeleted()))
                .map(MeetingNotice::getSubstance)
                .collect(Collectors.toList()));
    }

}




