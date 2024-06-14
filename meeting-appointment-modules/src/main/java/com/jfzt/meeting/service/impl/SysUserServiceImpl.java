package com.jfzt.meeting.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.code.kaptcha.Producer;
import com.jfzt.meeting.common.Result;
import com.jfzt.meeting.config.WxCpDefaultConfiguration;
import com.jfzt.meeting.constant.MessageConstant;
import com.jfzt.meeting.entity.SysUser;
import com.jfzt.meeting.entity.vo.SysUserVO;
import com.jfzt.meeting.exception.ErrorCodeEnum;
import com.jfzt.meeting.exception.RRException;
import com.jfzt.meeting.mapper.SysUserMapper;
import com.jfzt.meeting.service.SysUserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.cp.api.impl.WxCpServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;


/**
 * @author zilong.deng
 * @description 针对表【sys_user】的数据库操作Service实现
 * @createDate 2024-05-06 16:47:54
 */
@Slf4j
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser>
        implements SysUserService {


    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private Producer producer;
    @Autowired
    private WxCpServiceImpl wxCpService;
    @Autowired
    private WxCpDefaultConfiguration wxCpDefaultConfiguration;

    /**
     * 根据用户id拼接姓名字符串并返回用户信息集合
     * @param userIds 用户id集合
     * @param attendees 拼接姓名字符串
     * @param sysUserVOList 用户信息集合
     */
    @Override
    public void getUserInfo (List<String> userIds, StringBuffer attendees, List<SysUserVO> sysUserVOList) {
        //去重
        HashSet<String> userIdSet = new LinkedHashSet<>(userIds);
        ArrayList<String> userIdList = new ArrayList<>(userIdSet);
        userIdList.forEach(userId -> {
            SysUser user = this.getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUserId, userId));
            if (user != null) {
                //拼接参会人姓名
                if (sysUserVOList != null) {
                    sysUserVOList.add(new SysUserVO(user.getUserId(), user.getUserName(),null));
                }
                if (attendees != null) {
                    attendees.append(user.getUserName());
                    if (userIdList.indexOf(userId) < userIdList.size() - 1) {
                        attendees.append(",");
                    }
                }
            }
        });
    }


    /**
     * 根据权限等级查询企微用户是否为管理员
     * @param currentLevel 当前登录用户的权限等级
     * @return 用户信息集合
     */
    @Override
    public Result<List<SysUser>> selectAdmin (Integer currentLevel) {
        // 判断当前登录用户的权限等级
        if (MessageConstant.SUPER_ADMIN_LEVEL.equals(currentLevel)) {
            // 查询用户的信息
            List<SysUser> sysUsers = sysUserMapper.selectList(new QueryWrapper<>());
            return Result.success(sysUsers.stream()
                    .filter(user -> MessageConstant.ADMIN_LEVEL.equals(user.getLevel()))
                    .collect(Collectors.toList()));
        }
        return Result.fail(ErrorCodeEnum.SERVICE_ERROR_A0301);
    }

    /**
     * 新增管理员，修改用户的权限等级为1
     * @param userIds 用户id
     * @return 新增结果
     */
    @Override
    public Result<Integer> addAdmin (List<String> userIds) {
        int row = 0;
        if (userIds.isEmpty()){
            throw new RRException(ErrorCodeEnum.SERVICE_ERROR_A0410);
        }
        for (String userId : userIds) {
            SysUser sysUser = sysUserMapper.selectByUserId(userId);
            if (MessageConstant.ADMIN_LEVEL.equals(sysUser.getLevel())) {
                throw new RRException(ErrorCodeEnum.SERVICE_ERROR_A0400);
            }
            row += sysUserMapper.addAdmin(userId);
            log.info(userId);
            redisTemplate.opsForValue().getAndDelete("userInfo:" + userId);
        }
        if (row > 0) {
            return Result.success(row);
        } else {
            log.error("修改用户权限等级失败！");
            throw new RRException(ErrorCodeEnum.SERVICE_ERROR_A0421);
        }
    }


    /**
     * 删除管理员，修改用户的权限等级为2
     * @param userId 用户id
     * @return 删除结果
     */
    @Override
    public Result<Integer> deleteAdmin (String userId) {
        if (userId.isEmpty()){
            throw new RRException(ErrorCodeEnum.SERVICE_ERROR_A0410);
        }
        SysUser sysUser = sysUserMapper.selectByUserId(userId);
        if (MessageConstant.EMPLOYEE_LEVEL.equals(sysUser.getLevel())) {
            throw new RRException(ErrorCodeEnum.SERVICE_ERROR_A0400);
        }
        int row = sysUserMapper.deleteAdmin(userId);
        redisTemplate.opsForValue().getAndDelete("userInfo:" + userId);

        if (row > 0) {
            return Result.success(row);
        }
        log.error("修改用户权限等级失败！");
        throw new RRException(ErrorCodeEnum.SERVICE_ERROR_A0421);
    }

    /**
     * 返回验证码图片，存储到redis
     * @param uuid 唯一标识
     * @return BufferedImage
     */
    @Override
    public BufferedImage getCaptcha (String uuid) {
        String code = producer.createText();
        redisTemplate.opsForValue().set("uuid:"+uuid, code, Duration.ofSeconds(60));
        return producer.createImage(code);
    }

    /**
     * 查询用户信息
     * @param userId 用户id
     * @return 用户信息
     **/
    @Override
    public SysUser findUser (String userId) {

        LambdaQueryWrapper<SysUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SysUser::getUserId, userId);
        return sysUserMapper.selectOne(lambdaQueryWrapper);
    }

    /**
     * 模糊查询成员
     * @param name 成员名称
     * @return 成员信息集合
     */
    @Override
    public Result<List<SysUserVO>> findByName (String name) {
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

    /**
     * QR code 返回前端二维码
     * @return 二维码
     */
    @Override
    public Map<String, String> userQrCode() {
        String url = wxCpService.buildQrConnectUrl(wxCpDefaultConfiguration.getUrl()+"/#/home",
                wxCpDefaultConfiguration.getState());
        Map<String,String> map = new HashMap<>(1);
        map.put("url",url);
        return map;
    }


}




