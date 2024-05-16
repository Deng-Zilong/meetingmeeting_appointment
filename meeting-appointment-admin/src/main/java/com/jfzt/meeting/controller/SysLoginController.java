package com.jfzt.meeting.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.jfzt.meeting.common.Result;
import com.jfzt.meeting.context.BaseContext;
import com.jfzt.meeting.entity.SysUser;
import com.jfzt.meeting.entity.vo.LoginVo;
import com.jfzt.meeting.entity.vo.UserInfoVO;
import com.jfzt.meeting.exception.ErrorCodeEnum;
import com.jfzt.meeting.exception.RRException;
import com.jfzt.meeting.service.SysUserService;
import com.jfzt.meeting.utils.TokenGenerator;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;


/**
 * 用户登录
 *
 * @author zhenxing.lu
 * @since 2024-04-30 10.13:51
 */
@Slf4j
@RestController
@RequestMapping("/meeting/user")
public class SysLoginController {

    @Autowired
    private SysUserService sysUserService;

    @Resource
    private RedisTemplate<String,Object> redisTemplate;



    /**
     * 验证码
     * @param uuid 唯一id
     * @throws IOException io异常
     */
    @GetMapping("captcha.jpg")
    public void captcha (HttpServletResponse response, @RequestParam("uuid") String uuid) throws IOException {
        response.setHeader("Cache-Control", "no-store, no-cache");
        response.setContentType("image/jpeg");
        //获取图片验证码
        BufferedImage image = sysUserService.getCaptcha(uuid);
        ServletOutputStream out = response.getOutputStream();
        ImageIO.write(image, "jpg", out);
        IOUtils.close(out);
    }

    /**
     * 用户登录
     * @param loginVo 登录信息
     * @return 登录结果
     */
    @PostMapping(value = "login")
    public Result<UserInfoVO> login(@RequestBody LoginVo loginVo) throws NoSuchAlgorithmException {
        //判断验证码是否正确,需要获取redis中的验证码
        String codeUuids = (String) redisTemplate.opsForValue().get(loginVo.getUuid());
        if(StringUtils.isBlank(codeUuids)||!loginVo.getCode().equalsIgnoreCase(codeUuids)){
            log.error("用户未请求验证码或者用户验证码输入错误");
            throw new  RRException(ErrorCodeEnum.SERVICE_ERROR_A0240);
        }
        SysUser sysUser = sysUserService.findUser(loginVo);
        if (sysUser == null ) {
            log.error("用户账户不存在");
            throw new  RRException(ErrorCodeEnum.SERVICE_ERROR_A0201);
        }
        if (!sysUser.getPassword().equals(loginVo.getPassword())){
            log.error("用户密码错误");
            throw new  RRException(ErrorCodeEnum.SERVICE_ERROR_A0210);
        }
        //生成一个token
        String accessToken = TokenGenerator.generateValue();
        //获取用户信息
        UserInfoVO userInfo = new UserInfoVO();
        userInfo.setAccessToken(accessToken);
        userInfo.setUserId(sysUser.getUserId());
        userInfo.setName(sysUser.getUserName());
        userInfo.setLevel(sysUser.getLevel());
        //存入到redis中
        redisTemplate.opsForValue().set("userInfo"+userInfo.getUserId(), JSONObject.toJSONString(userInfo), Duration.ofHours(24));
        //存入当前登录用户到ThreadLocal中
        BaseContext.setCurrentUserId(sysUser.getUserId());
        BaseContext.setCurrentLevel(sysUser.getLevel());
        return Result.success(userInfo);
    }

}
