package com.jfzt.meeting.controller;

import com.jfzt.meeting.common.Result;
import com.jfzt.meeting.constant.MessageConstant;
import com.jfzt.meeting.context.BaseContext;
import com.jfzt.meeting.entity.SysUser;
import com.jfzt.meeting.entity.vo.MeetingRoomVO;
import com.jfzt.meeting.entity.vo.UserInfoVO;
import com.jfzt.meeting.service.SysDepartmentUserService;
import com.jfzt.meeting.service.SysUserService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.cp.bean.WxCpUser;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.List;



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
     *
     * @param code 入参
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
     * 获取企业微信用户姓名
     */
    @GetMapping("/selectName")
    public Result<List<String>> selectList(SysUser sysUser){
        return sysUserService.selectAll(sysUser);

    }

    /**
     * 查询所有的管理员
     */
    @GetMapping("/selectAdmin")
    public Result<List<String>> selectAdmin(){
        return sysUserService.selectAdmin();
    }

    /**
     * 修改用户权限等级,只有超级管理员可以操作
     * @param userId 用户id
     * @param level 权限等级(0超级管理员，1管理员，2员工)
     */
    @PutMapping("/updateLevel")
    public Result<Object> updateStatus (@RequestParam("userId") String userId, @RequestParam("level") Integer level) {
        return sysUserService.updateLevel(userId, level);
    }
}
