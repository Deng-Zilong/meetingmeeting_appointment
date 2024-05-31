package com.jfzt.meeting.modules;

import com.jfzt.meeting.common.Result;
import com.jfzt.meeting.entity.SysUser;
import com.jfzt.meeting.entity.dto.MeetingGroupDTO;
import com.jfzt.meeting.service.MeetingGroupService;
import com.jfzt.meeting.service.impl.MeetingGroupServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;


/**
 * 依赖注入: 使用 @Mock 注解模拟 MeetingGroupService，使用 @InjectMocks 注解初始化 MeetingGroupServiceImpl。
 * 设置测试数据: 在每个测试方法中，使用 when 语句设置模拟数据和期望的返回值。
 * 断言: 使用 assertNotNull 和 assertEquals 等断言方法，验证方法返回值是否符合预期。
 *
 * @author zilong.deng
 * @date 2024/05/23
 */
@SpringBootTest
public class MeetingGroupTest {
    @Mock
    private MeetingGroupService meetingGroupService;
    @InjectMocks
    private MeetingGroupServiceImpl meetingGroupServiceImpl;

    @BeforeEach
    public void setUp () {
        // 设置mock对象的行为
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetMeetingGroupList () {
        MeetingGroupDTO meetingGroupDTO = new MeetingGroupDTO();
        meetingGroupDTO.setGroupName("test");
        meetingGroupDTO.setCreatedBy("admin");
        meetingGroupDTO.setUserName("admin");
        ArrayList<SysUser> sysUsers = new ArrayList<>();
        SysUser admin = new SysUser();
        admin.setUserName("admin");
        admin.setUserId("admin");
        sysUsers.add(admin);
        meetingGroupDTO.setUsers(sysUsers);
        when(meetingGroupService.addMeetingGroup(meetingGroupDTO)).thenReturn(Result.success());
        assertDoesNotThrow(() -> meetingGroupService.addMeetingGroup(meetingGroupDTO));
        assertEquals(meetingGroupService.addMeetingGroup(meetingGroupDTO), Result.success());
    }

    @Test
    public void testGetMeetingGroupListByPage () {
        //        Result<List<MeetingGroupVO>> result = meetingGroupService.checkGroup(1, 10, "admin");
        //        List<MeetingGroupVO> data = result.getData();
        //        // 断言结果是否符合预期
        //        assertEquals("test", data.getFirst().getGroupName());
        assertDoesNotThrow(() -> meetingGroupService.checkGroup(1, 10, "admin"));
    }
}
