package com.jfzt.meeting.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jfzt.meeting.common.Result;
import com.jfzt.meeting.entity.SysUser;
import com.jfzt.meeting.entity.vo.SysUserVO;
import org.apache.ibatis.annotations.Param;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;

/**
 *  针对表【sys_user】的数据库操作Service
 * @author zilong.deng
 * @since  2024-05-06 16:47:54
 */
public interface SysUserService extends IService<SysUser> {

    /**
     * 根据用户id拼接姓名字符串并返回用户信息集合
     * @param userIds 用户id集合
     * @param attendees 拼接姓名字符串
     * @param sysUserVOList 用户信息集合
     */
    void getUserInfo (List<String> userIds, StringBuffer attendees, List<SysUserVO> sysUserVOList);

    /**
     * 获取不是管理员的企业微信用户姓名
     * @param sysUser      用户信息
     * @param currentLevel 当前登录用户的权限等级
     * @return 用户姓名集合
     */
    Result<List<String>> selectAll (SysUser sysUser, Integer currentLevel);

    /**
     * 根据权限等级查询企微用户是否为管理员
     * @param currentLevel 当前登录用户的权限等级
     * @return 用户信息集合
     */
    Result<List<SysUser>> selectAdmin (Integer currentLevel);

    /**
     * 新增管理员，修改用户的权限等级为1
     * @param userIds 用户id
     * @return 新增结果
     */
    Result<Integer> addAdmin (@Param("userIds") List<String> userIds);

    /**
     * 删除管理员，修改用户的权限等级为2
     * @param userId 用户id
     * @return 删除结果
     */
    Result<Integer> deleteAdmin (@Param("userId") String userId);


    /**
     * 返回验证码图片，存储到redis
     * @param uuid 唯一标识
     * @return BufferedImage
     */
    BufferedImage getCaptcha (String uuid);

    /**
     * 查询用户信息
     * @param userId 用户id
     * @return 用户信息
     **/
    SysUser findUser (String userId);

    /**
     * 模糊查询成员
     * @param name 成员名称
     * @return 成员信息集合
     */
    Result<List<SysUserVO>> findByName (String name);

    /**
     * QR code 返回前端二维码
     * @return 二维码
     */
    Map<String, String> userQrCode();


}
