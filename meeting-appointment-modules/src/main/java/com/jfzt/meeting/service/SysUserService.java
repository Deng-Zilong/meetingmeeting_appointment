package com.jfzt.meeting.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jfzt.meeting.common.Result;
import com.jfzt.meeting.entity.SysUser;
import com.jfzt.meeting.entity.vo.LoginVo;
import com.jfzt.meeting.entity.vo.SysUserVO;
import org.apache.ibatis.annotations.Param;

import java.awt.image.BufferedImage;
import java.util.List;

/**
 * @author zilong.deng
 * @description 针对表【sys_user】的数据库操作Service
 * @createDate 2024-05-06 16:47:54
 */
public interface SysUserService extends IService<SysUser> {

    /**
     * 根据用户id拼接姓名字符串并返回用户信息集合
     */
    void getUserInfo (List<String> userIds, StringBuffer attendees, List<SysUserVO> sysUserVOList);

    /**
     * 获取不是管理员的企业微信用户姓名
     *
     * @param sysUser      用户信息
     * @param currentLevel 当前登录用户的权限等级
     * @return com.jfzt.meeting.common.Result<java.util.List < java.lang.String>>
     */
    Result<List<String>> selectAll (SysUser sysUser, Integer currentLevel);

    /**
     * 根据权限等级查询企微用户是否为管理员
     *
     * @param currentLevel 当前登录用户的权限等级
     * @return com.jfzt.meeting.common.Result<java.util.List < java.lang.String>>
     */
    Result<List<SysUser>> selectAdmin (Integer currentLevel);

    /**
     * 新增管理员，修改用户的权限等级为1
     *
     * @param userIds 用户id
     * @return com.jfzt.meeting.common.Result<java.lang.Integer>
     */
    Result<Integer> addAdmin (@Param("userIds") List<String> userIds);

    /**
     * 删除管理员，修改用户的权限等级为2
     *
     * @param userId 用户id
     * @return com.jfzt.meeting.common.Result<java.lang.Integer>
     */
    Result<Integer> deleteAdmin (@Param("userId") String userId);


    /**
     * uuid+生成验证码存储
     *
     * @param uuid
     */
    BufferedImage getCaptcha (String uuid);

    /**
     * 查询用户信息
     *
     * @param loginVo
     * @return
     */
    SysUser findUser (LoginVo loginVo);

    /**
     * @return com.jfzt.meeting.common.Result<java.util.List < com.jfzt.meeting.entity.vo.SysUserVO>>
     * @Description 模糊查询成员
     * @Param [name]
     */
    Result<List<SysUserVO>> findByName (String name);

    /**
     * QR code 返回前端二维码
     * @return
     */
    Map<String, String> userQrCode();

    String getUrlCode();
}
