package com.jfzt.meeting.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.jfzt.meeting.common.Result;
import com.jfzt.meeting.config.WxCpDefaultConfiguration;
import com.jfzt.meeting.context.BaseContext;
import com.jfzt.meeting.entity.SysUser;
import com.jfzt.meeting.entity.vo.LoginVo;
import com.jfzt.meeting.entity.vo.UserInfoVO;
import com.jfzt.meeting.exception.ErrorCodeEnum;
import com.jfzt.meeting.exception.RRException;
import com.jfzt.meeting.properties.JwtProperties;
import com.jfzt.meeting.service.SysUserService;
import com.jfzt.meeting.utils.EncryptUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
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
import java.util.HashMap;
import java.util.Map;

import static com.jfzt.meeting.utils.JwtUtil.createJWT;


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
    @Autowired
    private WxCpDefaultConfiguration wxCpDefaultConfiguration;
    @Autowired
    private JwtProperties jwtProperties;

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
    public Result<UserInfoVO> login (@Valid @RequestBody LoginVo loginVo) {
        //判断验证码是否正确,需要获取redis中的验证码
        String codeUuids = (String) redisTemplate.opsForValue().get("uuid:"+loginVo.getUuid());
        if (StringUtils.isBlank(codeUuids) || !loginVo.getCode().equalsIgnoreCase(codeUuids)) {
            log.error("用户未请求验证码或者用户验证码输入错误");
            throw new RRException(ErrorCodeEnum.SERVICE_ERROR_A0240);
        }
        SysUser sysUser = sysUserService.findUser(loginVo.getName());
        if (sysUser == null) {
            log.error("用户账户不存在");
            throw new RRException(ErrorCodeEnum.SERVICE_ERROR_A0201);
        }
        if (!EncryptUtils.match(loginVo.getPassword(), sysUser.getPassword())) {
            log.error("用户密码错误");
            throw new RRException(ErrorCodeEnum.SERVICE_ERROR_A0210);
        }
        JSONObject jsonObject = JSONObject.parseObject((String) redisTemplate.opsForValue().get("userInfo:" + loginVo.getName()));
        if (jsonObject != null){
            UserInfoVO userInfo = UserInfoVO.builder()
                    .accessToken(jsonObject.get("accessToken").toString())
                    .userId(jsonObject.get("userId").toString())
                    .name(jsonObject.get("name").toString())
                    .level((Integer) jsonObject.get("level"))
                    .url(wxCpDefaultConfiguration.getUrl())
                    .build();
            return Result.success(userInfo);
        }

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put("sysUserId", sysUser.getUserId());
        String token = createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);
        UserInfoVO userInfo = UserInfoVO.builder()
                .accessToken(token)
                .userId(sysUser.getUserId())
                .name(sysUser.getUserName())
                .level(sysUser.getLevel())
                .url(wxCpDefaultConfiguration.getUrl())
                .build();
        //存入到redis中
        redisTemplate.opsForValue().set("userInfo:"+userInfo.getUserId(),
                JSONObject.toJSONString(userInfo), Duration.ofHours(24));
        //存入当前登录用户到ThreadLocal中
        BaseContext.setCurrentUserId(sysUser.getUserId());
        BaseContext.setCurrentLevel(sysUser.getLevel());
        return Result.success(userInfo);
    }

    /**
     * 退出登录
     * @return Result
     */
    @GetMapping("delete")
    public Result delete (@RequestParam("userId") String userId){
//        redisTemplate.opsForValue().getAndDelete("userInfo:"+userId);
        return Result.success("删除成功");
    }


    /**
     *
     * @return Result<String>
     */
    @PutMapping("updateSysAdminPassword")
    public Result<String> addAdmin (@RequestParam String userId,
                                    @RequestParam String password)
            throws NoSuchAlgorithmException {
        SysUser one = sysUserService.getOne(new QueryWrapper<SysUser>().eq("user_id", userId));
        if (one == null) {
            SysUser sysUser = new SysUser();
            sysUser.setUserId(userId);
            sysUser.setUserName(userId);
            sysUser.setPassword(EncryptUtils.encrypt(EncryptUtils.md5encrypt(password)));
            sysUserService.save(sysUser);
        } else {
            one.setPassword(EncryptUtils.encrypt(EncryptUtils.md5encrypt(password)));
            log.info("MD5盐值加密后：{}", one.getPassword());
            sysUserService.updateById(one);
        }
        return Result.success("添加成功");
    }

}
