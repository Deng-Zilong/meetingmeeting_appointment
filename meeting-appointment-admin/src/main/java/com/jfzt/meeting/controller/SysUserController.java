package com.jfzt.meeting.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.jfzt.meeting.common.Result;
import com.jfzt.meeting.config.WxCpDefaultConfiguration;
import com.jfzt.meeting.entity.SysUser;
import com.jfzt.meeting.entity.dto.AdminDTO;
import com.jfzt.meeting.entity.vo.UserInfoVO;
import com.jfzt.meeting.exception.ErrorCodeEnum;
import com.jfzt.meeting.exception.RRException;
import com.jfzt.meeting.properties.JwtProperties;
import com.jfzt.meeting.service.SysDepartmentUserService;
import com.jfzt.meeting.service.SysUserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.cp.bean.WxCpUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static com.jfzt.meeting.utils.JwtUtil.createJWT;

/**
 * 获取企业信息
 *
 * @author zhenxing.lu
 * @since 2024-04-30 10.13:51
 */
@Slf4j
@RestController
@RequestMapping("/meeting/user")
public class SysUserController {


    @Resource
    private SysUserService sysUserService;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private WxCpDefaultConfiguration wxCpDefaultConfiguration;
    @Resource
    private SysDepartmentUserService sysDepartmentUserService;
    @Autowired
    private JwtProperties jwtProperties;



    /**
     * QR code 返回前端二维码
     * @return Result<Map<String, String>>
     */
    @GetMapping(value = "qr-code")
    public Result<Map<String, String>> qrCode(){
        //返回一个地址
        Map<String,String> map = sysUserService.userQrCode();

        return Result.success(map);
    }
    /**
     *
     * 获取token，部门，部门人员
     */
    @GetMapping(value = "info")
    @Transactional
    public Result<UserInfoVO> info(@RequestParam("code") String code) throws WxErrorException, NoSuchAlgorithmException {
        if (StringUtils.isBlank(code)) {
            log.error("用户扫码登录失败请重新扫码");
            throw new RRException(ErrorCodeEnum.SERVICE_ERROR_A02011);
        }
        //获取登录token
        String accessToken = sysDepartmentUserService.findTocken();
        log.info("获取登录token成功");
        //获取用户详细信息
        WxCpUser wxUser = sysDepartmentUserService.findUserName(accessToken, code);
        log.info("获取用户详细信息成功"+wxUser);
        //获取部门信息
        Long departmentLength = sysDepartmentUserService.findDepartment();
        log.info("获取部门信息成功");
        //获取部门人员
        sysDepartmentUserService.findDepartmentUser(departmentLength);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put("sysUserId", wxUser.getUserId());
        String token = createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);
        Integer userId = sysUserService.findUser(wxUser.getUserId()).getLevel();
        UserInfoVO userInfo = UserInfoVO.builder()
                .accessToken(token)
                .userId(wxUser.getUserId())
                .name(wxUser.getName())
                .level(userId)
                .url(wxCpDefaultConfiguration.getUrl())
                .build();
        redisTemplate.opsForValue().set("userInfo:" + userInfo.getUserId(), JSONObject.toJSONString(userInfo), Duration.ofHours(24));
        return Result.success(userInfo);
    }


    /**
     * 获取不是管理员的企业微信用户姓名
     *
     * @param sysUser      用户对象
     * @param currentLevel 当前登录用户的权限等级
     * @return com.jfzt.meeting.common.Result<java.util.List < java.lang.String>>
     */
    @GetMapping("/select-name")
    public Result<List<String>> selectList(SysUser sysUser, @RequestParam("currentLevel") Integer currentLevel) {
        return sysUserService.selectAll(sysUser, currentLevel);

    }


    /**
     * 查询所有的管理员
     *
     * @param currentLevel 当前登录用户的权限等级
     * @return com.jfzt.meeting.common.Result<java.util.List < java.lang.String>>
     */
    @GetMapping("/select-admin")
    public Result<List<SysUser>> selectAdmin(@RequestParam("currentLevel") Integer currentLevel) {
        return sysUserService.selectAdmin(currentLevel);
    }

    /**
     * 删除管理员,只有超级管理员可以操作
     *
     * @param adminDTO 用户DTO对象
     * @return com.jfzt.meeting.common.Result<java.lang.Integer>
     */
    @PutMapping("/delete-admin")
    public Result<Integer> deleteAdmin(@RequestBody AdminDTO adminDTO) {
        return sysUserService.deleteAdmin(adminDTO.getUserId());
    }


    /**
     * 新增管理员,只有超级管理员可以操作
     *
     * @param adminDTO 用户DTO对象
     * @return com.jfzt.meeting.common.Result<java.lang.Integer>
     */
    @PutMapping("/add-admin")
    public Result<Integer> addAdmin(@RequestBody AdminDTO adminDTO) {
        return sysUserService.addAdmin(adminDTO.getUserIds());
    }
}
