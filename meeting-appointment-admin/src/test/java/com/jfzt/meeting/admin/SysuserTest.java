package com.jfzt.meeting.admin;


import com.jfzt.meeting.service.SysUserService;
import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;



import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
public class SysuserTest {



    @Autowired
    private SysUserService sysUserService;


    /**
     * 返回企业微信二维码地址
     */
//    @Test
//    public void qrCode() {
//        //返回一个地址
//        assertDoesNotThrow(() -> sysUserService.userQrCode());
//
//    }




}

