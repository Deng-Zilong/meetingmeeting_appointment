package com.jfzt.meeting.modules;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jfzt.meeting.common.Result;
import com.jfzt.meeting.constant.MeetingRoomStatusConstant;
import com.jfzt.meeting.constant.MessageConstant;
import com.jfzt.meeting.entity.DeviceErrorMessage;
import com.jfzt.meeting.entity.MeetingDevice;
import com.jfzt.meeting.entity.MeetingRecord;
import com.jfzt.meeting.entity.MeetingRoom;
import com.jfzt.meeting.entity.SysUser;
import com.jfzt.meeting.entity.dto.DatePeriodDTO;
import com.jfzt.meeting.entity.dto.MeetingRoomDTO;
import com.jfzt.meeting.entity.vo.MeetingRoomOccupancyVO;
import com.jfzt.meeting.entity.vo.MeetingRoomSelectionVO;
import com.jfzt.meeting.entity.vo.MeetingRoomVO;
import com.jfzt.meeting.entity.vo.TimePeriodOccupancyVO;
import com.jfzt.meeting.exception.ErrorCodeEnum;
import com.jfzt.meeting.exception.RRException;
import com.jfzt.meeting.mapper.MeetingRoomMapper;
import com.jfzt.meeting.mapper.SysUserMapper;
import com.jfzt.meeting.service.MeetingDeviceService;
import com.jfzt.meeting.service.DeviceErrorMessageService;
import com.jfzt.meeting.service.MeetingRecordService;
import com.jfzt.meeting.service.impl.MeetingRoomServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class MeetingRoomTest {
    /**
     * 测试方法依赖的对象
     */
    @Mock
    private SysUserMapper sysUserMapper;

    @MockBean
    private MeetingRecordService meetingRecordService;

    @MockBean
    private MeetingRoomMapper meetingRoomMapper;
    @MockBean
    private MeetingDeviceService meetingDeviceService;

    @MockBean
    private DeviceErrorMessageService deviceErrorMessageService;

    // 会议室模拟数据
     private MeetingRoom meetingRoom;
    private MeetingRoom pausedRoom;
    private MeetingRoom normalRoom;


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
                .isDeleted(0)
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
                .build();
        meetingRoom = MeetingRoom.builder()
                .id(3L)
                .roomName("会议室3")
                .location("地点3")
                .capacity(10)
                .status(1)
                .gmtCreate(LocalDateTime.now())
                .gmtModified(LocalDateTime.now())
                .createdBy("user3")
                .isDeleted(0)
                .build();


        pausedRoom = new MeetingRoom();
        pausedRoom.setId(2L);
        pausedRoom.setStatus(MeetingRoomStatusConstant.MEETINGROOM_STATUS_PAUSE);
        normalRoom = new MeetingRoom();
        normalRoom.setId(1L);
        normalRoom.setStatus(MeetingRoomStatusConstant.MEETINGROOM_STATUS_AVAILABLE);

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
                1L, 1, "admin", "admin",
                LocalDateTime.now(), LocalDateTime.now(), 0);
        MeetingRecord meetingRecord2 = new MeetingRecord(2L, "会议2", "会议描述2",
                LocalDateTime.of(2024, 6, 20, 9, 0),
                LocalDateTime.of(2024, 6, 20, 12, 0),
                1L, 1, "admin", "admin",
                LocalDateTime.now(), LocalDateTime.now(), 0);
        MeetingRecord meetingRecord3 = new MeetingRecord(2L, "会议2", "会议描述2",
                LocalDateTime.of(2024, 6, 19, 9, 0),
                LocalDateTime.of(2024, 6, 19, 12, 0),
                2L, 1, "admin", "admin",
                LocalDateTime.now(), LocalDateTime.now(), 0);
        MeetingRecord meetingRecord4 = new MeetingRecord(2L, "会议2", "会议描述2",
                LocalDateTime.of(2024, 6, 20, 9, 0),
                LocalDateTime.of(2024, 6, 20, 10, 0),
                2L, 1, "admin", "admin",
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
        assertEquals("未占用", unoccupied.getTimePeriod());
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
                1L, 1, "admin", "admin",
                LocalDateTime.now(), LocalDateTime.now(), 0);
        ArrayList<MeetingRecord> meetingRecords = new ArrayList<>();
        meetingRecords.add(meetingRecord);
        when(meetingRecordService.list(Mockito.<LambdaQueryWrapper<MeetingRecord>>any())).thenReturn(meetingRecords);
        ArrayList<MeetingDevice> meetingDevices = new ArrayList<>();
        meetingDevices.add(MeetingDevice.builder().id(1L).roomId(1L).deviceName("投影仪").status(1).build());
        when(meetingDeviceService.list(Mockito.<LambdaQueryWrapper<MeetingDevice>>any())).thenReturn(meetingDevices);
        ArrayList<DeviceErrorMessage> deviceErrorMessages1 = new ArrayList<>();
        deviceErrorMessages1.add(DeviceErrorMessage.builder().id(1L).userId("admin").info("坏了").build());
        Result<List<DeviceErrorMessage>> deviceErrorMessages = Result.success(deviceErrorMessages1);
        when(deviceErrorMessageService.getInfo(1L)).thenReturn(deviceErrorMessages);
        Result<List<MeetingRoomVO>> availableMeetingRooms = meetingRoomServiceImpl.getAvailableMeetingRooms(
                LocalDateTime.of(2024, 6, 19, 9, 0),
                LocalDateTime.of(2024, 6, 19, 10, 0));
        //断言结果，时间段内只有会议室2未被占用
        assertEquals(2, availableMeetingRooms.getData().getFirst().getId());
        assertEquals("投影仪异常：坏了", availableMeetingRooms.getData().getFirst().getDeviceExceptionInfo().getFirst());

    }

    /**
     * 测试查询被禁用的会议室Id，管理员权限
     */
    @Test
    public void selectUsableRoom() {
        List<MeetingRoom> allRooms = Arrays.asList(normalRoom,pausedRoom);
        Mockito.when(meetingRoomMapper.selectList(Mockito.any(QueryWrapper.class))).thenReturn(allRooms);
        Result<List<Long>> result = meetingRoomServiceImpl.selectUsableRoom(MessageConstant.SUPER_ADMIN_LEVEL);
        Assertions.assertEquals(Result.success().getCode(), result.getCode());
        Assertions.assertEquals(Collections.singletonList(2L), result.getData());
    }

    /**
     * 测试查询被禁用的会议室Id，没有权限
     */
    @Test
    public void selectUsableRoomNoLevel() {
        List<MeetingRoom> allRooms = Arrays.asList(normalRoom,pausedRoom);
        Mockito.when(meetingRoomMapper.selectList(Mockito.any(QueryWrapper.class))).thenReturn(allRooms);
        assertThrows(Exception.class, () -> meetingRoomServiceImpl.selectUsableRoom(MessageConstant.EMPLOYEE_LEVEL));
    }


    /**
     * 测试更新会议室信息成功
     */
    @Test
    public void testUpdateRoomSuccess() {
        MeetingRoomDTO meetingRoomDTO = new MeetingRoomDTO();
        meetingRoomDTO.setId(1L);
        meetingRoomDTO.setRoomName("room1");
        meetingRoomDTO.setLocation("Location");
        meetingRoomDTO.setCapacity(50);
        meetingRoomDTO.setStatus(1);
        meetingRoomDTO.setCurrentLevel(MessageConstant.SUPER_ADMIN_LEVEL);

        when(meetingRoomMapper.selectById(1L)).thenReturn(meetingRoom);
        when(meetingRoomMapper.updateRoom(anyLong(), anyString(), anyString(), anyInt(), anyInt())).thenReturn(1);

        Result<Integer> result = meetingRoomServiceImpl.updateRoom(meetingRoomDTO);
        assertNotNull(result);
        assertEquals(1, result.getData().intValue());
    }

    /**
     * 测试更新会议室信息，当传入的数据为空时
     */
    @Test
    public void testUpdateRoomNullValues() {
        MeetingRoomDTO meetingRoomDTO = new MeetingRoomDTO(); // 创建一个空的DTO对象
        meetingRoomDTO.setRoomName("");
        meetingRoomDTO.setCapacity(null);
        meetingRoomDTO.setStatus(null);
        meetingRoomDTO.setCurrentLevel(null);
        assertThrows(Exception.class, () -> meetingRoomServiceImpl.updateRoom(meetingRoomDTO));
    }

    /**
     * 测试更新会议室信息，当会议室位置超过最大长度时
     */
    @Test
    public void testUpdateRoomFailLocation() {
        MeetingRoomDTO meetingRoomDTO = new MeetingRoomDTO();
        meetingRoomDTO.setId(1L);
        meetingRoomDTO.setCapacity(50);
        meetingRoomDTO.setStatus(1);
        meetingRoomDTO.setCurrentLevel(MessageConstant.SUPER_ADMIN_LEVEL);
        meetingRoomDTO.setRoomName("Room1");
        meetingRoomDTO.setLocation("A very long location description that exceeds the limit"); // 超过长度限制

        Result<Integer> result = meetingRoomServiceImpl.updateRoom(meetingRoomDTO);

        assertEquals("会议室位置长度不能超过30个字符！", result.getMsg());
    }

    /**
     * 测试更新会议室信息，当会议室名称超过最大长度时
     */
    @Test
    public void testUpdateRoomFailRoomName() {
        MeetingRoomDTO meetingRoomDTO = new MeetingRoomDTO();
        meetingRoomDTO.setId(1L);
        meetingRoomDTO.setCapacity(50);
        meetingRoomDTO.setStatus(1);
        meetingRoomDTO.setCurrentLevel(MessageConstant.SUPER_ADMIN_LEVEL);
        meetingRoomDTO.setRoomName("A very long room name that exceeds the limit"); // 超过长度限制
        meetingRoomDTO.setLocation("location");

        Result<Integer> result = meetingRoomServiceImpl.updateRoom(meetingRoomDTO);
        assertEquals("会议室名称长度不能超过15个字符！", result.getMsg());
    }


    /**
     * 测试更新会议室信息，当会议室名称重复时
     */
/*    @Test
    public void testUpdateRoomNameDuplicate() {
        // 修改之前的会议室信息
        MeetingRoomDTO meetingRoomDTO = new MeetingRoomDTO();
        meetingRoomDTO.setId(3L);
        meetingRoomDTO.setRoomName("会议室3");
        meetingRoomDTO.setCapacity(50);
        meetingRoomDTO.setStatus(1);
        meetingRoomDTO.setCurrentLevel(MessageConstant.SUPER_ADMIN_LEVEL);
        meetingRoomDTO.setLocation("location");

        // 修改之后的会议室信息
        MeetingRoomDTO meetingRoomDTO1 = new MeetingRoomDTO();
        meetingRoomDTO.setId(3L);
        meetingRoomDTO.setRoomName("会议室3333");
        meetingRoomDTO.setCapacity(20);
        meetingRoomDTO.setStatus(1);
        meetingRoomDTO.setCurrentLevel(MessageConstant.SUPER_ADMIN_LEVEL);
        meetingRoomDTO.setLocation("location1");

        // 模拟数据库中已存在同名会议室
        List<MeetingRoom> roomList = new ArrayList<>();
        roomList.add(meetingRoom);


        when(meetingRoomMapper.selectById(meetingRoomDTO.getId())).thenReturn(meetingRoom);

        when(meetingRoomMapper.selectList(any())).thenReturn(roomList);

        Result<Integer> result = meetingRoomServiceImpl.updateRoom(meetingRoomDTO);
        assertEquals("会议室名称重复！", result.getMsg());

    }*/

/*    @Test
    public void updateRoom_Fail_NameDuplicate() {
        MeetingRoomDTO meetingRoomDTO = new MeetingRoomDTO();
        meetingRoomDTO.setId(3L); // Different ID to simulate duplicate name scenario
        meetingRoomDTO.setRoomName("Test Room");
        meetingRoomDTO.setLocation("Test Location");
        meetingRoomDTO.setCapacity(10);
        meetingRoomDTO.setStatus(1);
        meetingRoomDTO.setCurrentLevel(MessageConstant.SUPER_ADMIN_LEVEL);

        when(meetingRoomMapper.selectById(3L)).thenReturn(meetingRoom);
        when(meetingRoomMapper.selectList(any())).thenReturn(mock(List.class)); // Simulate non-empty list for duplicate check

        Result<Integer> result = meetingRoomServiceImpl.updateRoom(meetingRoomDTO);

        assertNull(result.getData());
        assertEquals("会议室名称重复！", result.getMsg());
        // Verifying if the updateRoom method does not proceed to update due to duplicate name check
        verify(meetingRoomMapper, never()).updateRoom(anyLong(), anyString(), anyString(), anyInt(), anyInt());
    }*/

    /**
     * 测试更新会议室信息，当用户权限不足时
     */
    @Test
    public void testUpdateRoomNoLevel() {
        MeetingRoomDTO meetingRoomDTO = new MeetingRoomDTO();
        meetingRoomDTO.setId(1L);
        meetingRoomDTO.setRoomName("会议室3");
        meetingRoomDTO.setCapacity(50);
        meetingRoomDTO.setStatus(1);
        meetingRoomDTO.setCurrentLevel(MessageConstant.EMPLOYEE_LEVEL);
        meetingRoomDTO.setLocation("location");

        assertThrows(Exception.class, () -> meetingRoomServiceImpl.updateRoom(meetingRoomDTO));
    }

    /**
     * 测试添加会议室参数为空情况
     */
    @Test
    public void testParametersEmpty() {
        MeetingRoom emptyRoom = new MeetingRoom();
        emptyRoom.setRoomName("");
        emptyRoom.setCapacity(null);
        assertThrows(RRException.class, () -> meetingRoomServiceImpl.addMeetingRoom(emptyRoom));
    }

    /**
     * 测试添加会议室会议室名称或位置过长的情况
     */
    @Test
    public void testAddMeetingRoomNameTooLong() {
        MeetingRoom room = MeetingRoom.builder()
                .roomName("MeetingRoomNameMeetingRoomName") // 超过最大长度限制
                .location("VeryLongLocationName VeryLongLocationName") // 超过最大长度限制
                .capacity(10)
                .id(1L)
                .gmtCreate(LocalDateTime.now())
                .gmtModified(LocalDateTime.now())
                .isDeleted(0)
                .status(MeetingRoomStatusConstant.MEETINGROOM_STATUS_AVAILABLE)
                .createdBy("admin")
                .build();
        Result<Integer> result = meetingRoomServiceImpl.addMeetingRoom(room);
        assertNotNull(result);
        assertEquals("会议室位置长度或会议室名称过长！", result.getMsg());
    }

    /**
     * 测试添加会议室重复的会议室名称
     */
    @Test
    public void testAddMeetingRoomSameRoomName() {
        // 模拟数据库返回已存在的会议室名称
        when(meetingRoomMapper.selectList(any())).thenReturn(List.of(meetingRoom));
        assertThrows(RRException.class, () -> meetingRoomServiceImpl.addMeetingRoom(meetingRoom));
    }

    /**
     * 测试添加会议室成功
     */
    @Test
    public void testAddMeetingRoomSuccess() {
        // 模拟查询创建者用户信息为管理员
        when(sysUserMapper.selectByUserId("user3")).thenReturn(
                new SysUser(1L, "user3", "user3", "1111111", MessageConstant.SUPER_ADMIN_LEVEL));
        // 模拟插入操作返回成功
        when(meetingRoomMapper.insert(any())).thenReturn(1);
        Result<Integer> result = meetingRoomServiceImpl.addMeetingRoom(meetingRoom);
        assertNotNull(result);
        assertEquals(1, result.getData().intValue());
    }

    /**
     * 测试添加会议室，当用户没有权限时
     */
    @Test
    public void testAddMeetingRoom_WhenUserHasInsufficientPermissions_ReturnsPermissionError() {
        // 模拟查询创建者用户信息为普通用户
        when(sysUserMapper.selectByUserId("user3")).thenReturn(
                new SysUser(1L, "user3", "user3", "1111111", MessageConstant.EMPLOYEE_LEVEL));

        Result<Integer> result = meetingRoomServiceImpl.addMeetingRoom(meetingRoom);
        assertNotNull(result);
        assertEquals(ErrorCodeEnum.SERVICE_ERROR_A0301.getCode(), result.getCode());
    }

    /**
     * 测试删除会议室成功
     */
    @Test
    public void testDeleteMeetingRoomSuccess() {
        // Mock 数据
        Long roomId = 1L;
        Integer currentLevel = MessageConstant.ADMIN_LEVEL;
        when(meetingRoomMapper.selectById(roomId)).thenReturn(meetingRoom);
        when(meetingRoomMapper.deleteById(roomId)).thenReturn(1);
        when(meetingDeviceService.remove(any())).thenReturn(true);

        // 调用被测方法
        Result<Integer> result = meetingRoomServiceImpl.deleteMeetingRoom(roomId, currentLevel);

        // 验证结果
        assertEquals(Result.success(1), result);
        verify(meetingRoomMapper, times(1)).selectById(roomId);
        verify(meetingRoomMapper, times(1)).deleteById(roomId);
        verify(meetingDeviceService, times(1)).remove(any());
    }

    /**
     * 测试删除会议室失败，当会议室不存在时
     */
    @Test
    public void testDeleteMeetingRoomNotFound() {
        // Mock 数据
        Long roomId = 1L;
        Integer currentLevel = MessageConstant.ADMIN_LEVEL;
        when(meetingRoomMapper.selectById(roomId)).thenReturn(null);

        assertThrows(RRException.class, () -> meetingRoomServiceImpl.deleteMeetingRoom(roomId, currentLevel));
    }

    /**
     * 测试删除会议室失败
     */
    @Test
    public void testDeleteMeetingRoomDeleteFailed() {
        // Mock 数据
        Long roomId = 1L;
        Integer currentLevel = MessageConstant.ADMIN_LEVEL;
        when(meetingRoomMapper.selectById(roomId)).thenReturn(meetingRoom);
        when(meetingRoomMapper.deleteById(roomId)).thenReturn(0);

        assertThrows(RRException.class, () -> meetingRoomServiceImpl.deleteMeetingRoom(roomId, currentLevel));
    }

    /**
     * 测试删除会议室失败，当用户没有权限时
     */
    @Test
    public void testDeleteMeetingRoomUnauthorized() {
        // Mock 数据
        Long roomId = 1L;
        Integer currentLevel = MessageConstant.EMPLOYEE_LEVEL;

        // 调用被测方法
        assertThrows(RRException.class, () -> meetingRoomServiceImpl.deleteMeetingRoom(roomId, currentLevel));
    }

    /**
     * 测试更新会议室状态失败，当会议室不存在时
     */
    @Test
    public void testUpdateStatusFailRoomIdNotExists() {
        MeetingRoomDTO meetingRoomDTO = new MeetingRoomDTO();
        meetingRoomDTO.setId(1L); // 假设不存在的会议室ID

        // 模拟查询会议室返回 null
        when(meetingRoomMapper.selectById(1L)).thenReturn(null);

        assertThrows(RRException.class, () -> meetingRoomServiceImpl.updateStatus(meetingRoomDTO));
    }

    /**
     * 测试更新会议室状态失败，当参数为空时
     */
    @Test
    public void testUpdateStatusFailStatusOrCurrentLevelNull() {
        MeetingRoomDTO meetingRoomDTO = new MeetingRoomDTO();
        meetingRoomDTO.setId(1L);
        meetingRoomDTO.setStatus(null);
        meetingRoomDTO.setCurrentLevel(null);

        assertThrows(RRException.class, () -> meetingRoomServiceImpl.updateStatus(meetingRoomDTO));
    }

    /**
     * 测试更新会议室状态成功
     */
    @Test
    public void testUpdateStatusSuccess() {
        MeetingRoomDTO meetingRoomDTO = new MeetingRoomDTO();
        meetingRoomDTO.setId(3L);
        meetingRoomDTO.setStatus(1);
        meetingRoomDTO.setCurrentLevel(MessageConstant.SUPER_ADMIN_LEVEL);
        meetingRoomDTO.setRoomName("会议室3");
        meetingRoomDTO.setLocation("地点3");
        meetingRoomDTO.setCapacity(10);

        when(meetingRoomMapper.selectById(3L)).thenReturn(meetingRoom);

        // 模拟更新操作返回成功
        when(meetingRoomMapper.updateStatus(3L, 1)).thenReturn(1);

        Result<Integer> result = meetingRoomServiceImpl.updateStatus(meetingRoomDTO);

        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.getData().intValue());
    }


    /**
     * 测试更新会议室状态失败，当用户没有权限时
     */
    @Test
    public void testUpdateStatus_WhenUserHasInsufficientPermissions_ThrowsException() {
        MeetingRoomDTO meetingRoomDTO = new MeetingRoomDTO();
        meetingRoomDTO.setId(3L);
        meetingRoomDTO.setStatus(1);
        meetingRoomDTO.setCurrentLevel(MessageConstant.EMPLOYEE_LEVEL); // 模拟普通用户权限

        // 模拟查询会议室存在
        when(meetingRoomMapper.selectById(3L)).thenReturn(meetingRoom);

        assertThrows(RRException.class, () -> meetingRoomServiceImpl.updateStatus(meetingRoomDTO));
    }





    @AfterEach
    public void tearDown () {
        // 清理测试数据
    }
}
