package com.jfzt.meeting.modules;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jfzt.meeting.common.Result;
import com.jfzt.meeting.constant.MessageConstant;
import com.jfzt.meeting.entity.SysUser;
import com.jfzt.meeting.entity.vo.SysUserVO;
import com.jfzt.meeting.exception.ErrorCodeEnum;
import com.jfzt.meeting.exception.RRException;
import com.jfzt.meeting.mapper.SysUserMapper;
import com.jfzt.meeting.service.impl.SysUserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class SysUserTest {

    @MockBean
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

    /**
     * 测试查询管理员
     */
    @Test
    public void testSelectAdmin() {
        // 测试超级管理员权限
        when(sysUserMapper.selectList(Mockito.<LambdaQueryWrapper<SysUser>>any())).thenReturn(mockAdminUsers);
        Result<List<SysUser>> superAdminResult = sysUserServiceImpl.selectAdmin(MessageConstant.SUPER_ADMIN_LEVEL);
        assertNotNull(superAdminResult);
        assertFalse(superAdminResult.getData().isEmpty());
        assertEquals(MessageConstant.ADMIN_LEVEL, superAdminResult.getData().get(0).getLevel());
        // 重置数据
        reset(sysUserMapper);

        // 重置模拟并使用非超级管理员级别进行测试
        when(sysUserMapper.selectList(Mockito.<LambdaQueryWrapper<SysUser>>any())).thenReturn(Collections.emptyList());
        Result<List<SysUser>> nonSuperAdminResult = sysUserServiceImpl.selectAdmin(null);
        assertNotNull(nonSuperAdminResult);
        assertEquals(ErrorCodeEnum.SERVICE_ERROR_A0301.getDescription(), nonSuperAdminResult.getMsg());
        // 重置数据
        reset(sysUserMapper);

        // 未找到管理员用户时进行测试
        when(sysUserMapper.selectList(Mockito.<LambdaQueryWrapper<SysUser>>any())).thenReturn(Collections.emptyList());
        Result<List<SysUser>> noAdminsResult = sysUserServiceImpl.selectAdmin(MessageConstant.SUPER_ADMIN_LEVEL);
        assertNotNull(noAdminsResult);
        assertTrue(noAdminsResult.getData().isEmpty());
    }

    /**
     * 测试addAdmin方法，userIds为空
     */
    @Test
    void addAdminUserIdsIsEmpty() {
        assertThrows(RRException.class, () ->
                sysUserServiceImpl.addAdmin(Collections.emptyList()), "Expected addAdmin to throw, but it didn't");
    }

    /**
     * 测试addAdmin方法，userIds包含一个已存在的超级管理员
     */
    @Test
    void addAdminAlreadyAdminTest() {
        List<String> userIds = Collections.singletonList("existingAdmin");
        SysUser adminUser = new SysUser();
        adminUser.setLevel(MessageConstant.ADMIN_LEVEL);

        when(sysUserMapper.selectByUserId(userIds.get(0))).thenReturn(adminUser);

        assertThrows(RRException.class, () -> sysUserServiceImpl.addAdmin(userIds), "Expected addAdmin to throw when user is already an admin, but it didn't");
    }

    /**
     * 测试addAdmin方法，userIds为空
     */
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


    /**
     * 测试deleteAdmin方法，userId为空
     */
    @Test
    public void testDeleteAdmin_UserIdEmpty() {
        // 调用userService的方法，传入空的userId，期待抛出异常
        assertThrows(RRException.class, () -> sysUserServiceImpl.deleteAdmin(""));
    }

    /**
     * 测试deleteAdmin方法，用户不是管理员级别
     */
    @Test
    public void testDeleteAdmin_UserNotAdminLevel() {
        // 准备测试数据
        String userId = "456";
        SysUser mockUser = new SysUser();
        mockUser.setUserId(userId);
        mockUser.setLevel(MessageConstant.EMPLOYEE_LEVEL);

        // 模拟sysUserMapper的行为
        when(sysUserMapper.selectByUserId(userId)).thenReturn(mockUser);

        // 调用userService的方法，期待抛出异常
        assertThrows(RRException.class, () -> sysUserServiceImpl.deleteAdmin(userId));
    }



    /**
     * 测试findUser方法，成功
     */
    @Test
    public void testFindUser_Success() {
        // 准备测试数据
        String userId = "123";
        SysUser mockUser = new SysUser();
        mockUser.setUserId(userId);
        mockUser.setUserName("testUser");
        // 模拟sysUserMapper的行为
        when(sysUserMapper.selectOne(Mockito.<LambdaQueryWrapper<SysUser>>any())).thenReturn(mockUser);
        // 调用userService的方法进行测试
        SysUser result = sysUserServiceImpl.findUser(userId);
        // 验证结果
        assertEquals(userId, result.getUserId());
        assertEquals("testUser", result.getUserName());
        // 验证sysUserMapper的selectOne方法是否被调用且参数正确
        verify(sysUserMapper, times(1)).selectOne(Mockito.<LambdaQueryWrapper<SysUser>>any());
    }

    /**
     * 测试findUser方法，未找到用户
     */
    @Test
    public void testFindUser_NotFound() {
        // 准备测试数据
        String userId = "456";
        // 模拟sysUserMapper的行为，返回null表示未找到用户
        when(sysUserMapper.selectOne(Mockito.<LambdaQueryWrapper<SysUser>>any())).thenReturn(null);
        // 调用userService的方法进行测试
        SysUser result = sysUserServiceImpl.findUser(userId);
        // 验证结果
        assertNull(result);
        // 验证sysUserMapper的selectOne方法是否被调用且参数正确
        verify(sysUserMapper, times(1)).selectOne(Mockito.<LambdaQueryWrapper<SysUser>>any());
    }


    /**
     * 测试findByName方法
     */
    @Test
    public void testFindByName() {

        // 模拟的输入参数
        String name = "test";

        // 构造模拟数据
        SysUser sysUser1 = new SysUser();
        sysUser1.setUserId("user1");
        sysUser1.setUserName("testUser1");

        SysUser sysUser2 = new SysUser();
        sysUser2.setUserId("user2");
        sysUser2.setUserName("testUser2");

        List<SysUser> mockUsers = Arrays.asList(sysUser1, sysUser2);

        // 设置Mockito行为
        when(sysUserMapper.selectList(Mockito.<LambdaQueryWrapper<SysUser>>any())).thenReturn(mockUsers);

        // 调用被测试方法
        Result<List<SysUserVO>> result = sysUserServiceImpl.findByName(name);

        // 验证调用
        verify(sysUserMapper, times(1)).selectList(Mockito.<LambdaQueryWrapper<SysUser>>any());

        // 验证返回结果是否符合预期
        assertEquals(2, result.getData().size());

        // 验证转换是否正确
        List<String> userNames = result.getData().stream()
                .map(SysUserVO::getUserName)
                .collect(Collectors.toList());
        assertEquals(Arrays.asList("testUser1", "testUser2"), userNames);
    }
}

