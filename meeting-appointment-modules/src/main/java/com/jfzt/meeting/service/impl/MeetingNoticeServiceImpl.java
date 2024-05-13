package com.jfzt.meeting.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jfzt.meeting.common.Result;
import com.jfzt.meeting.constant.MessageConstant;
import com.jfzt.meeting.context.BaseContext;
import com.jfzt.meeting.entity.MeetingNotice;
import com.jfzt.meeting.exception.ErrorCodeEnum;
import com.jfzt.meeting.exception.RRException;
import com.jfzt.meeting.mapper.MeetingNoticeMapper;
import com.jfzt.meeting.service.MeetingNoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.jfzt.meeting.context.BaseContext.removeCurrentLevel;

/**
 * @author xuchang.yang
 * @description 针对表【meeting_notice(会议公告表)】的数据库操作Service实现
 * @since 2024-04-28 11:45:33
 */
@Service
public class MeetingNoticeServiceImpl extends ServiceImpl<MeetingNoticeMapper, MeetingNotice>
        implements MeetingNoticeService {

    @Autowired
    private MeetingNoticeMapper meetingNoticeMapper;


    /**
     * 上传公告
     * @param meetingNotice 公告信息
     * @return com.jfzt.meeting.common.Result<java.lang.Integer>
     */
    @Override
    public Result<Integer> addNotice(MeetingNotice meetingNotice) {
        // 获取当前登录用户的权限等级
        Integer level = BaseContext.getCurrentLevel();
        removeCurrentLevel();
        if (MessageConstant.SUPER_ADMIN_LEVEL.equals(level) || MessageConstant.ADMIN_LEVEL.equals(level)){
            String currentId = BaseContext.getCurrentId();
            meetingNotice.setUserId(currentId);
            int insert = meetingNoticeMapper.insert(meetingNotice);
            if (insert > 0){
                return Result.success();
            }
            log.error("新增公告失败！");
            throw new RRException(ErrorCodeEnum.SERVICE_ERROR_A0421);
        }
        return Result.fail(ErrorCodeEnum.SERVICE_ERROR_A0301);
    }



    /**
     * 查询所有通告信息
     * @param meetingNotice 公告信息
     * @return com.jfzt.meeting.common.Result<java.util.List<java.lang.String>>
     */
    @Override
    public Result<List<String>> selectAll(MeetingNotice meetingNotice) {
        List<MeetingNotice> meetingNotices = meetingNoticeMapper.selectList(new QueryWrapper<>());
        return Result.success(meetingNotices.stream()
                .map(MeetingNotice::getSubstance)
                .collect(Collectors.toList()));
    }

}




