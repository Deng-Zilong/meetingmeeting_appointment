package com.jfzt.meeting.controller;

import com.jfzt.meeting.common.Result;
import com.jfzt.meeting.service.SysDepartmentUserService;
import jakarta.annotation.Resource;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.cp.bean.WxCpUser;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


/**
 * 获取企业信息
 *
 * @author zhenxing.lu
 * @since 2024-04-30 10.13:51
 */

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
    public Result login(@RequestParam("code") String code) throws WxErrorException {
        Map<String, String> userInfo = new HashMap<>();
        //获取登录tocken
        String accessToken = sysDepartmentUserService.findTocken();
        //获取用户详细信息
        WxCpUser user = sysDepartmentUserService.findUserName(accessToken, code);
        //获取部门信息
        Long departmentLength = sysDepartmentUserService.findDepartment();
        //获取部门人员
        sysDepartmentUserService.findDepartmentUser(departmentLength);
        userInfo.put("access_token", accessToken);
        return Result.success(userInfo);
    }




}
