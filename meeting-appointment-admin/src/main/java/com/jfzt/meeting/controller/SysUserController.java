package com.jfzt.meeting.controller;

import com.jfzt.meeting.common.Result;
import com.jfzt.meeting.service.SysDepartmentUserService;
import jakarta.annotation.Resource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 获取企业信息
 *
 * @author zhenxing.lu
 * @since 2024-04-30 10.13:51
 */

@CrossOrigin
@RestController
@RequestMapping("login")
public class SysUserController {

    @Resource
    private SysDepartmentUserService sysDepartmentUserService;

    /**
     * 获取token，部门，部门人员
     *
     * @param code 入参
     */
    @GetMapping(value = "logininfo")
    @Transactional
    public Result login(@RequestParam("code") String code) {
        //获取登录tocken
        String access_token = sysDepartmentUserService.findTocken();
        //获取用户详细信息
        Map<String,String> userinfo = sysDepartmentUserService.findUserName(access_token,code);
        //获取部门信息
        int departmentLength = sysDepartmentUserService.findDepartment(access_token);
        //获取部门人员
        sysDepartmentUserService.findDepartmentUser(access_token, departmentLength);
        userinfo.put("access_token",access_token);
        return Result.success(userinfo);
    }

}
