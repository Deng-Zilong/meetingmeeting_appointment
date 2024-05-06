package com.jfzt.meeting.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jfzt.meeting.entity.SysDepartmentUser;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.cp.bean.WxCpUser;

import java.util.List;

/**
 * @author zilong.deng
 * @description 针对表【sys_department_user(企微部门成员关联表)】的数据库操作Service
 * @createDate 2024-04-29 14:46:29
 */
public interface SysDepartmentUserService extends IService<SysDepartmentUser> {

    String findTocken () throws WxErrorException;

    WxCpUser findUserName (String access_token, String code) throws WxErrorException;

    Long findDepartment () throws WxErrorException;

    void findDepartmentUser (Long departmentLength) throws WxErrorException;


    /**
     * 根据用户id集合获取用户姓名拼接字符串
     *
     * @param userIds 用户id
     * @return {@code String}
     */
    String getNamesByIds (List<String> userIds);

}
