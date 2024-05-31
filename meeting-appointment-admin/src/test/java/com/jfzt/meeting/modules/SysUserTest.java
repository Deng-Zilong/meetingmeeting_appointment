package com.jfzt.meeting.modules;

import com.jfzt.meeting.exception.RRException;
import com.jfzt.meeting.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
public class SysUserTest {

    @Autowired
    private SysUserService sysUserService;
    /**
     * 测试查询所有管理员
     */
    @Test
    public void selectAdmin() {
        assertNotNull(sysUserService.selectAdmin(0));
        assertNull(sysUserService.selectAdmin(1).getData());
        assertNull(sysUserService.selectAdmin(2).getData());
    }

    /**
     * 测试删除管理员
     */
    @Test
    public void deleteAdmin() {
        assertThrows(RRException.class, () -> sysUserService.deleteAdmin(""));
    }


    /**
     * 测试新增管理员
     */
    @Test
    public void addAdmin() {
        List<String> list = new ArrayList<>();
        assertThrows(RRException.class, () -> sysUserService.addAdmin(list));
    }
}
