package com.jfzt.meeting.modules;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jfzt.meeting.common.Result;
import com.jfzt.meeting.constant.MessageConstant;
import com.jfzt.meeting.entity.SysUser;
import com.jfzt.meeting.exception.ErrorCodeEnum;
import com.jfzt.meeting.exception.RRException;
import com.jfzt.meeting.mapper.SysUserMapper;
import com.jfzt.meeting.service.impl.SysUserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class SysUserTest {

    @Mock
    private SysUserMapper sysUserMapper;

    @InjectMocks
    private SysUserServiceImpl sysUserServiceImpl;

    private List<SysUser> mockAdminUsers;

    @BeforeEach
    void setUp() {
        SysUser adminUser = new SysUser();
        adminUser.setId(1L);
        adminUser.setUserId("admin");
        adminUser.setUserName("Admin");
        adminUser.setLevel(MessageConstant.ADMIN_LEVEL);
        mockAdminUsers = Collections.singletonList(adminUser);
    }

    @Test
    void testSelectAdmin() {
        // 测试超级管理员权限
        when(sysUserMapper.selectList(any(QueryWrapper.class))).thenReturn(mockAdminUsers);
        Result<List<SysUser>> superAdminResult = sysUserServiceImpl.selectAdmin(MessageConstant.SUPER_ADMIN_LEVEL);
        assertNotNull(superAdminResult);
        assertFalse(superAdminResult.getData().isEmpty());
        assertEquals(MessageConstant.ADMIN_LEVEL, superAdminResult.getData().get(0).getLevel());
        // 重置数据
        reset(sysUserMapper);

        // 重置模拟并使用非超级管理员级别进行测试
        when(sysUserMapper.selectList(any(QueryWrapper.class))).thenReturn(Collections.emptyList());
        Result<List<SysUser>> nonSuperAdminResult = sysUserServiceImpl.selectAdmin(null);
        assertNotNull(nonSuperAdminResult);
        assertEquals(ErrorCodeEnum.SERVICE_ERROR_A0301.getDescription(), nonSuperAdminResult.getMsg());
        // 重置数据
        reset(sysUserMapper);

        // 未找到管理员用户时进行测试
        when(sysUserMapper.selectList(any(QueryWrapper.class))).thenReturn(Collections.emptyList());
        Result<List<SysUser>> noAdminsResult = sysUserServiceImpl.selectAdmin(MessageConstant.SUPER_ADMIN_LEVEL);
        assertNotNull(noAdminsResult);
        assertTrue(noAdminsResult.getData().isEmpty());
    }

        @Test
        void addAdminUserIdsIsEmpty() {
            assertThrows(RRException.class, () ->
                    sysUserServiceImpl.addAdmin(Collections.emptyList()), "Expected addAdmin to throw, but it didn't");
        }

        @Test
        void addAdminAlreadyAdminTest() {
            List<String> userIds = Collections.singletonList("existingAdmin");
            SysUser adminUser = new SysUser();
            adminUser.setLevel(MessageConstant.ADMIN_LEVEL);

            when(sysUserMapper.selectByUserId(userIds.get(0))).thenReturn(adminUser);

            assertThrows(RRException.class, () -> sysUserServiceImpl.addAdmin(userIds), "Expected addAdmin to throw when user is already an admin, but it didn't");
        }


        @Test
        void addAdminNoUsersAddTest() {
            List<String> userIds = Arrays.asList("user1", "user2");
            SysUser adminUser = new SysUser();
            adminUser.setLevel(MessageConstant.ADMIN_LEVEL);

            // 模拟一个用户已经是管理员，而另一个用户不存在
            when(sysUserMapper.selectByUserId("user1")).thenReturn(adminUser);

            Exception duplicateNameException = assertThrows(RRException.class, () -> sysUserServiceImpl.addAdmin(userIds));
            assertTrue(duplicateNameException.getMessage().contains("用户请求参数错误"));

        }
}

