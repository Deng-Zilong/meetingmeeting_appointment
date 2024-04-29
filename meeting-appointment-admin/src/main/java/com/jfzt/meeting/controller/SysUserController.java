package com.jfzt.meeting.controller;

import com.jfzt.meeting.common.Result;
import com.jfzt.meeting.service.SysDepartmentUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author zilong.deng
 * @date 2024/04/29
 */
@RestController
@RequestMapping("login")
public class SysUserController {

    @Autowired
    private SysDepartmentUserService sysUserService;


    /**
     * 获取企业信息
     *
     * @param code
     * @param appid
     * @return
     */
    @GetMapping(value = "logininfo")
    @Transactional
    public Result login (@RequestParam("code") String code, @RequestParam("appid") String appid) {
        //获取登录tocken
        String access_token = sysUserService.findTocken();
        //获取企业部门用户
        sysUserService.findDepartment(access_token, code);
        return null;
    }
}
