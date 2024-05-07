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
@CrossOrigin
@RestController
@RequestMapping("/meeting/user")
public class SysUserController {

    @Resource
    private SysDepartmentUserService sysDepartmentUserService;
    /**
     * 获取token，部门，部门人员
     *
     * @param code 入参
     * @return
     */
    @GetMapping(value = "info")
    @Transactional
    public Result<Map<String, String>> info(@RequestParam("code") String code) throws WxErrorException {
        String testName = "admin";
        Map<String, String> userInfo = new HashMap<>(2);
        //获取登录token
        String accessToken = sysDepartmentUserService.findTocken();
        //获取用户详细信息
        WxCpUser wxUser = sysDepartmentUserService.findUserName(accessToken, code);
        if (wxUser.getUserId().equals(testName)){
            //获取部门信息
            Long departmentLength = sysDepartmentUserService.findDepartment();
            //获取部门人员
            sysDepartmentUserService.findDepartmentUser(departmentLength);
        }
        userInfo.put("access_token", accessToken);
        userInfo.put("userId", wxUser.getUserId());
        userInfo.put("name", wxUser.getName());
        userInfo.put("email", wxUser.getEmail());
        return Result.success(userInfo);
    }
    @GetMapping(value = "login")
    public Result<Map<String, String>> login(){
        return null;

    }

}
