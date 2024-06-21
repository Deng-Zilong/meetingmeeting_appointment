package com.jfzt.meeting.modules;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jfzt.meeting.common.Result;
import com.jfzt.meeting.entity.MeetingRecord;
import com.jfzt.meeting.entity.MeetingRoom;
import com.jfzt.meeting.entity.dto.DatePeriodDTO;
import com.jfzt.meeting.entity.vo.MeetingRoomOccupancyVO;
import com.jfzt.meeting.entity.vo.MeetingRoomSelectionVO;
import com.jfzt.meeting.entity.vo.MeetingRoomVO;
import com.jfzt.meeting.entity.vo.TimePeriodOccupancyVO;
import com.jfzt.meeting.mapper.MeetingRoomMapper;
import com.jfzt.meeting.service.MeetingRecordService;
import com.jfzt.meeting.service.impl.MeetingRoomServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest
public class MeetingRoomTest {

    /**
     * 测试方法依赖的对象
     */
    @MockBean
    private MeetingRecordService meetingRecordService;
    @MockBean
    private MeetingRoomMapper meetingRoomMapper;

    /**
     * 测试方法所在对象
     */
    @InjectMocks
    private MeetingRoomServiceImpl meetingRoomServiceImpl;

    /**
     * 测试方法执行前
     **/
    @BeforeEach
    public void setUp () {
        //        初始化Mockito注解
        MockitoAnnotations.openMocks(this);

        //生成模拟数据
        MeetingRoom room1 = MeetingRoom.builder()
                .id(1L)
                .roomName("会议室1")
                .location("地点1")
                .capacity(10)
                .status(1)
                .gmtCreate(LocalDateTime.now())
                .gmtModified(LocalDateTime.now())
                .createdBy("user1")
                .isDeleted(0).equipment("设备1")
                .build();
        MeetingRoom room2 = MeetingRoom.builder()
                .id(2L)
                .roomName("会议室2")
                .location("地点2")
                .capacity(15)
                .status(1)
                .gmtCreate(LocalDateTime.now())
                .gmtModified(LocalDateTime.now())
                .createdBy("user2")
                .isDeleted(0)
                .equipment("设备2")
                .build();

        List<MeetingRoom> meetingRooms = Arrays.asList(room1, room2);
        //调用操作数据库的方法时返回指定的模拟数据
        when(meetingRoomMapper.getByRoomId(anyLong())).thenAnswer(invocation -> {
            // 获取传入的RoomId参数
            Long roomId = invocation.getArgument(0);
            // 遍历meetingRooms查找对应的MeetingRoom对象
            for (MeetingRoom room : meetingRooms) {
                if (room.getId().equals(roomId)) {
                    // 返回匹配的MeetingRoom对象
                    return room;
                }
            }
            // 如果未找到对应的RoomId，则返回null
            return null;
        });
        when(meetingRoomServiceImpl.list(Mockito.<LambdaQueryWrapper<MeetingRoom>>any())).thenReturn(meetingRooms);
        // MeetingRecord 模拟数据
        MeetingRecord meetingRecord1 = new MeetingRecord(1L, "会议1", "会议描述1",
                LocalDateTime.of(2024, 6, 19, 10, 0),
                LocalDateTime.of(2024, 6, 19, 16, 0),
                1L, 1, "admin",
                LocalDateTime.now(), LocalDateTime.now(), 0);
        MeetingRecord meetingRecord2 = new MeetingRecord(2L, "会议2", "会议描述2",
                LocalDateTime.of(2024, 6, 20, 9, 0),
                LocalDateTime.of(2024, 6, 20, 12, 0),
                1L, 1, "admin",
                LocalDateTime.now(), LocalDateTime.now(), 0);
        MeetingRecord meetingRecord3 = new MeetingRecord(2L, "会议2", "会议描述2",
                LocalDateTime.of(2024, 6, 19, 9, 0),
                LocalDateTime.of(2024, 6, 19, 12, 0),
                2L, 1, "admin",
                LocalDateTime.now(), LocalDateTime.now(), 0);
        MeetingRecord meetingRecord4 = new MeetingRecord(2L, "会议2", "会议描述2",
                LocalDateTime.of(2024, 6, 20, 9, 0),
                LocalDateTime.of(2024, 6, 20, 10, 0),
                2L, 1, "admin",
                LocalDateTime.now(), LocalDateTime.now(), 0);

        List<MeetingRecord> meetingRecords = Arrays.asList(meetingRecord1, meetingRecord2, meetingRecord3, meetingRecord4);
        when(meetingRecordService.list(Mockito.<LambdaQueryWrapper<MeetingRecord>>any())).thenReturn(meetingRecords);

    }

    @Test
    public void getMeetingRoomOccupancyTest () {
        //设置参数
        DatePeriodDTO datePeriodDTO = new DatePeriodDTO();
        datePeriodDTO.setStartDate(LocalDate.of(2024, 6, 19));
        datePeriodDTO.setEndDate(LocalDate.of(2024, 6, 20));

        //调用要测试的方法
        Result<List<MeetingRoomOccupancyVO>> allMeetingRoomOccupancy = meetingRoomServiceImpl
                .getAllMeetingRoomOccupancy(datePeriodDTO);

        //断言结果
        assertDoesNotThrow(() -> meetingRoomServiceImpl.getAllMeetingRoomOccupancy(new DatePeriodDTO()));

        assertNotNull(allMeetingRoomOccupancy);
        assertNotNull(allMeetingRoomOccupancy.getData());
        assertNotNull(allMeetingRoomOccupancy.getData().getFirst());

        MeetingRoomOccupancyVO occupancyVO = allMeetingRoomOccupancy.getData().getFirst();
        assertEquals(9, occupancyVO.getTotalOccupancy());
        assertEquals(18, occupancyVO.getTotal());
        assertEquals((float) 9 / 18, occupancyVO.getTotalOccupancyRate());
        assertNotNull(occupancyVO.getTimePeriods());
        TimePeriodOccupancyVO unoccupied = occupancyVO.getTimePeriods().getFirst();
        TimePeriodOccupancyVO top1 = occupancyVO.getTimePeriods().get(1);
        TimePeriodOccupancyVO others = occupancyVO.getTimePeriods().get(4);
        assertEquals("unoccupied", unoccupied.getTimePeriod());
        assertEquals("10:00-11:00", top1.getTimePeriod());
        assertEquals("others", others.getTimePeriod());
        assertEquals(2, top1.getOccupied());
        assertEquals((float) 2 / 18, top1.getOccupancyRate());
    }

    /**
     * 测试getAllMeetingRoomProportion方法
     **/
    @Test
    public void getAllMeetingRoomProportionTest () {
        //设置参数
        DatePeriodDTO datePeriodDTO = new DatePeriodDTO();
        datePeriodDTO.setStartDate(LocalDate.of(2024, 6, 19));
        datePeriodDTO.setEndDate(LocalDate.of(2024, 6, 20));
        Result<List<MeetingRoomSelectionVO>> allMeetingRoomProportion = meetingRoomServiceImpl
                .getAllMeetingRoomProportion(datePeriodDTO);

        //断言结果
        assertEquals(2, allMeetingRoomProportion.getData().size());
        assertEquals(4, allMeetingRoomProportion.getData().getFirst().getTotal());
        assertEquals(2, allMeetingRoomProportion.getData().getFirst().getSelected());
    }

    /**
     * 测试getAvailableMeetingRooms方法
     **/
    @Test
    public void getAvailableMeetingRoomsTest () {
        //模拟的条件查询结果
        MeetingRecord meetingRecord = new MeetingRecord(2L, "会议2", "会议描述2",
                LocalDateTime.of(2024, 6, 20, 9, 0),
                LocalDateTime.of(2024, 6, 20, 12, 0),
                1L, 1, "admin",
                LocalDateTime.now(), LocalDateTime.now(), 0);
        ArrayList<MeetingRecord> meetingRecords = new ArrayList<>();
        meetingRecords.add(meetingRecord);
        when(meetingRecordService.list(Mockito.<LambdaQueryWrapper<MeetingRecord>>any())).thenReturn(meetingRecords);
        Result<List<MeetingRoomVO>> availableMeetingRooms = meetingRoomServiceImpl.getAvailableMeetingRooms(
                LocalDateTime.of(2024, 6, 19, 9, 0),
                LocalDateTime.of(2024, 6, 19, 10, 0));
        //断言结果，时间段内只有会议室2未被占用
        assertEquals(2, availableMeetingRooms.getData().getFirst().getId());

    }


    @AfterEach
    public void tearDown () {
        // 清理测试数据
    }
}
