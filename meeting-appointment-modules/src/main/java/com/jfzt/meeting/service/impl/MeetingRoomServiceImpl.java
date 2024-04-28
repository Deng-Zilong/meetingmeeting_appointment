package com.jfzt.meeting.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jfzt.meeting.common.Result;
import com.jfzt.meeting.entity.MeetingRoom;
import com.jfzt.meeting.entity.vo.MeetingRoomVO;
import com.jfzt.meeting.mapper.MeetingRoomMapper;
import com.jfzt.meeting.service.MeetingRoomService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zilong.deng
 * @description 针对表【meeting_room(会议室表)】的数据库操作Service实现
 * @createDate 2024-04-28 11:50:45
 */
@Slf4j
@Service
public class MeetingRoomServiceImpl extends ServiceImpl<MeetingRoomMapper, MeetingRoom>
        implements MeetingRoomService {


    /**
     * @param meetingRoomVO 会议室对象
     * @return {@code Boolean}
     */
    @Override
    public Result<String> addMeetingRoom (MeetingRoomVO meetingRoomVO) {
        List<MeetingRoom> list = this.list(new LambdaQueryWrapper<MeetingRoom>().eq(MeetingRoom::getLocation, meetingRoomVO.getLocation()));
        if (!list.isEmpty()) {
            //location重复，添加失败
            log.error("{}:location重复，添加失败", meetingRoomVO);
            return Result.fail("location重复，添加失败");
        }
        MeetingRoom room = new MeetingRoom();
        room.setCapacity(meetingRoomVO.getCapacity());
        room.setRoomName(meetingRoomVO.getRoomName());
        room.setLocation(meetingRoomVO.getLocation());
        room.setCreatedBy(meetingRoomVO.getUserId());
        boolean saved = this.save(room);
        if (saved) {
            return Result.success("添加成功");
        } else {
            return Result.fail("添加失败");

        }
    }

    @Override
    public Boolean deleteMeetingRoom (MeetingRoom meetingRoom) {
        return null;
    }
}




