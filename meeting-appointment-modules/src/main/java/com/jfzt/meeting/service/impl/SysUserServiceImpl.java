package com.jfzt.meeting.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.code.kaptcha.Producer;
import com.jfzt.meeting.common.Result;
import com.jfzt.meeting.entity.SysUser;
import com.jfzt.meeting.entity.vo.LoginVo;
import com.jfzt.meeting.entity.vo.SysUserVO;
import com.jfzt.meeting.mapper.SysUserMapper;
import com.jfzt.meeting.service.SysUserService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zilong.deng
 * @description 针对表【sys_user】的数据库操作Service实现
 * @createDate 2024-05-06 16:47:54
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser>
        implements SysUserService {

    /**
     * 定义权限等级(0超级管理员，1管理员，2员工)
     */
    public static final Integer SUPER_ADMIN_LEVEL = 0;
    public static final Integer ADMIN_LEVEL = 1;
    public static final Integer EMPLOYEE_LEVEL= 2;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Resource
    private RedisTemplate<String,String> redisTemplate;
    @Resource
    private Producer producer;


    /**
     * 获取所有不是管理员的企业微信用户的姓名
     * @param sysUser
     * @return
     */
    @Override
    public List<String> selectAll(SysUser sysUser) {
        List<SysUser> sysUsers = sysUserMapper.selectList(new QueryWrapper<>());
        return sysUsers.stream()
                .filter(user -> !ADMIN_LEVEL.equals(user.getLevel()) && !SUPER_ADMIN_LEVEL.equals(user.getLevel()))
                .map(SysUser::getUserName)
                .collect(Collectors.toList());
    }

    /**
     * 查询所有的管理员
     * @return
     */
    @Override
    public List<String> selectAdmin() {
        List<SysUser> sysUsers = sysUserMapper.selectList(new QueryWrapper<>());
        return sysUsers.stream()
                .filter(user -> ADMIN_LEVEL.equals(user.getLevel()) || SUPER_ADMIN_LEVEL.equals(user.getLevel()))
                .map(SysUser::getUserName)
                .collect(Collectors.toList());
    }

    /**
     * 修改用户权限等级
     * @param userName
     * @param level
     * @return
     */
    @Override
    public boolean updateLevel(String userName, Integer level) {
        return sysUserMapper.updateLevel(userName, level);
    }

    @Override
    public BufferedImage getCaptcha(String uuid) {
        //生成文字验证码
        String code =uuid+"/"+producer.createText();
        redisTemplate.opsForValue().set("codeUuid"+uuid,code, Duration.ofSeconds(60));
        return producer.createImage(code);
    }

    @Override
    public SysUser findUser(LoginVo loginVo) {

        LambdaQueryWrapper<SysUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SysUser::getUserId,loginVo.getName());
        SysUser sysUser = sysUserMapper.selectOne(lambdaQueryWrapper);
        return sysUser;
    }

    /**
     * @Description 模糊查询成员
     * @Param [name]
     * @return com.jfzt.meeting.common.Result<java.util.List<com.jfzt.meeting.entity.vo.SysUserVO>>
     * @exception
     */
    @Override
    public Result<List<SysUserVO>> findByName(String name) {
        // 根据用户名查询用户信息，并将其转换为SysUserVO对象
        return Result.success(lambdaQuery()
                // 判断用户名是否为空
                .like(StringUtils.isNotBlank(name), SysUser::getUserName, name)
                // 获取查询结果
                .list()
                // 将查询结果转换为流
                .stream()
                // 遍历流，将每个用户信息转换为SysUserVO对象
                .map(sysUser -> SysUserVO.builder()
                        // 调用builder的userId方法，传入用户id
                        .userId(sysUser.getUserId())
                        // 调用builder的userName方法，传入用户名
                        .userName(sysUser.getUserName())
                        // 调用builder的build方法，构建SysUserVO对象
                        .build())
                // 将SysUserVO对象collect到列表中
                .collect(Collectors.toList()));
    }
}




