package com.jfzt.meeting.controller;

import com.alibaba.fastjson.JSONObject;
import com.jfzt.meeting.common.Result;

import com.jfzt.meeting.entity.SysUser;
import com.jfzt.meeting.entity.vo.UserInfoVO;
import com.jfzt.meeting.service.SysDepartmentUserService;
import com.jfzt.meeting.service.SysUserService;
import jakarta.annotation.Resource;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.cp.bean.WxCpUser;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.List;
import java.util.Map;


/**
 * 获取企业信息
 *
 * @author zhenxing.lu
 * @since 2024-04-30 10.13:51
 */

@RestController
@RequestMapping("/meeting/user")
public class SysUserController {


    @Resource
    private SysUserService sysUserService;

    @Resource
    private RedisTemplate<String,String> redisTemplate;

    @Resource
    private SysDepartmentUserService sysDepartmentUserService;


    /**
     * 获取token，部门，部门人员
     * @return userInfoVO
     */
    @GetMapping(value = "info")
    @Transactional
    public Result<UserInfoVO> info(@RequestParam("code") String code) throws WxErrorException {
        String testName = "admin";
        UserInfoVO userInfoVO = new UserInfoVO();
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
        userInfoVO.setAccessToken(accessToken);
        userInfoVO.setUserId(wxUser.getUserId());
        userInfoVO.setName(wxUser.getName());
        userInfoVO.setLevel(wxUser.getIsLeader());
        redisTemplate.opsForValue().set("userInfo"+wxUser.getUserId(), String.valueOf(userInfoVO), Duration.ofHours(2));
        return Result.success(userInfoVO);
    }


    /**
     * 获取不是管理员的企业微信用户姓名
     * @param sysUser 用户对象
     * @param currentLevel 当前登录用户的权限等级
     * @return com.jfzt.meeting.common.Result<java.util.List<java.lang.String>>
     */
    @GetMapping("/selectName")
    public Result<List<String>> selectList(SysUser sysUser, @RequestParam("currentLevel") Integer currentLevel){
        return sysUserService.selectAll(sysUser, currentLevel);

    }


    /**
     * 查询所有的管理员
     * @param currentLevel 当前登录用户的权限等级
     * @return com.jfzt.meeting.common.Result<java.util.List<java.lang.String>>
     */
    @GetMapping("/selectAdmin")
    public Result<List<SysUser>> selectAdmin(@RequestParam("currentLevel") Integer currentLevel){
        return sysUserService.selectAdmin(currentLevel);
    }

    /**
     * 删除管理员,只有超级管理员可以操作
     * @param userId 用户id
     * @return com.jfzt.meeting.common.Result<java.lang.Integer>
     */
    @PutMapping("/deleteAdmin")
    public Result<Integer> deleteAdmin (@RequestParam("userId") String userId) {
        return sysUserService.deleteAdmin(userId);
    }


    /**
     * 新增管理员,只有超级管理员可以操作
     * @param userIds 用户id
     * @return com.jfzt.meeting.common.Result<java.lang.Integer>
     */
    @PutMapping("/addAdmin")
    public Result<Integer> addAdmin (@RequestParam("userIds") List<String> userIds) {
        return sysUserService.addAdmin(userIds);
    }
}
