package com.jfzt.meeting.admin;

import com.jfzt.meeting.common.Result;
import com.jfzt.meeting.config.WxCpDefaultConfiguration;
import com.jfzt.meeting.exception.RRException;
import com.jfzt.meeting.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.cp.api.impl.WxCpServiceImpl;
import me.chanjar.weixin.cp.tp.service.WxCpTpService;
import me.chanjar.weixin.cp.tp.service.impl.WxCpTpOAServiceImpl;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.naming.directory.DirContext;
import javax.naming.directory.SearchResult;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
@SpringBootTest
public class SysuserTest {


    @Autowired
    private WxCpServiceImpl wxCpService;
    @Autowired
    private WxCpDefaultConfiguration wxCpDefaultConfiguration;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private WxCpTpService wxCpTpService;

    /**
     * 返回企业微信二维码地址
     */
    @Test
    public void qrCode() {
        //返回一个地址
//        assertThrows(RRException.class, () -> sysUserService.userQrCode());
        assertDoesNotThrow(() -> sysUserService.userQrCode());

    }

//    @Test
//    public void qrCode1() {
//        ADUserUtils utils = new ADUserUtils();
//        utils.searchInformation(utils.root);
//
////        utils.add("JimGreen");
//        utils.close();
//    }


}

