package com.jfzt.meeting.controller;

import com.jfzt.meeting.common.Result;
import com.jfzt.meeting.context.BaseContext;
import com.jfzt.meeting.entity.SysUser;
import com.jfzt.meeting.entity.vo.LoginVo;
import com.jfzt.meeting.entity.vo.UserInfoVO;
import com.jfzt.meeting.service.SysUserService;
import com.jfzt.meeting.utils.TokenGenerator;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;


/**
 * 用户登录
 *
 * @author zhenxing.lu
 * @since 2024-04-30 10.13:51
 */



@RestController
@RequestMapping("/meeting/user")
public class SysLoginController {

    @Autowired
    private SysUserService sysUserService;

    @Resource
    private RedisTemplate<String,String> redisTemplate;


    /**
     * 验证码
     */
    @GetMapping("captcha.jpg")
    public void captcha(HttpServletResponse response, String uuid) throws IOException {
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
     */
    @PostMapping(value = "login")
    public Result<UserInfoVO> login(@RequestBody LoginVo loginVo) throws NoSuchAlgorithmException {
        //判断验证码是否正确,需要获取redis中的验证码
        String codeUuids = redisTemplate.opsForValue().get("codeUuid" + loginVo.getUuid());
        String[] codeUuid = codeUuids.split("/");

        if (!loginVo.getUuid().equals(codeUuid[0]) || !loginVo.getCode().equals(codeUuid[1])) {
            return Result.fail("验证码错误");
        }
        SysUser sysUser = sysUserService.findUser(loginVo);
        if (sysUser == null || !sysUser.getPassword().equals(loginVo.getPassword())) {
            return Result.fail("账号或密码不正确");
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
        redisTemplate.opsForValue().set("userInfo"+userInfo.getUserId(), String.valueOf(userInfo));
        //存入当前登录用户到ThreadLocal中
        BaseContext.setCurrentUserId(sysUser.getUserId());
        return Result.success(userInfo);
    }

}
