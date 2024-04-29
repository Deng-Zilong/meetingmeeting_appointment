package com.jfzt.meeting.controller;


import com.jfzt.meeting.common.Result;
import com.jfzt.meeting.service.SysUserEnterpriseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * 用户企业微信表
 *
 * @author zhenxing.lu
 * @description :参会人员
 * @sine 2024-04-24 11:03:26
 */

@RestController
@RequestMapping("login")
public class SysUserEnterpriseController {
    @Autowired
    private SysUserEnterpriseService sysUserEnterpriseService;

    /**
     * @return 返回String前端二维码
     */
    //
    @GetMapping(value = "/qrCode")
    public String qrCode () {
        String qrCode = sysUserEnterpriseService.findQrCode();
        return qrCode;
    }

    /**
     * 获取企业信息
     *
     * @param code
     * @param state
     * @return
     */
    @GetMapping(value = "/user")
    @Transactional
    public Result<String> login (@RequestParam("code") String code, @RequestParam("state") String state) {
        //获取登录tocken
        String access_token = sysUserEnterpriseService.findTocken();
        //获取登录用户信息
        sysUserEnterpriseService.findUserName(access_token, code);
        //获取企业部门用户
        sysUserEnterpriseService.findDepartment(access_token, code);
        return null;
    }

}
