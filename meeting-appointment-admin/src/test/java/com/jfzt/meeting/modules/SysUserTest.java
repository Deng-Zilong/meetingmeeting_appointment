package com.jfzt.meeting.modules;

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
     * 测试查询所有的管理员
     */
    @Test
    public void selectAdmin() {
        assertNull(sysUserService.selectAdmin(0));
        assertNull(sysUserService.selectAdmin(1));
        assertNull(sysUserService.selectAdmin(2));
    }

    /**
     * 测试删除管理员
     */
    @Test
    public void deleteAdmin() {
        assertEquals("00000", sysUserService.deleteAdmin("QianRuoXiaMo").getCode());
    }


    /**
     * 测试新增管理员,只有超级管理员可以操作
     */
    @Test
    public void addAdmin() {
        List<String> list = new ArrayList<>();
        list.add("QianRuoXiaMo");
        list.add("XingChen");
        assertEquals("00000", sysUserService.addAdmin(list).getCode());
    }
}
