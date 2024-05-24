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
     * ���Բ�ѯ���еĹ���Ա
     */
    @Test
    public void selectAdmin() {
        assertNull(sysUserService.selectAdmin(0));
        assertNull(sysUserService.selectAdmin(1));
        assertNull(sysUserService.selectAdmin(2));
    }

    /**
     * ����ɾ������Ա
     */
    @Test
    public void deleteAdmin() {
        assertEquals("00000", sysUserService.deleteAdmin("QianRuoXiaMo").getCode());
    }


    /**
     * ������������Ա,ֻ�г�������Ա���Բ���
     */
    @Test
    public void addAdmin() {
        List<String> list = new ArrayList<>();
        list.add("QianRuoXiaMo");
        list.add("XingChen");
        assertEquals("00000", sysUserService.addAdmin(list).getCode());
    }
}
