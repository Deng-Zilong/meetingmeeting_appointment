package com.jfzt.meeting.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jfzt.meeting.entity.SysDepartmentUser;

import java.util.Map;

/**
 * @author zilong.deng
 * @description 针对表【sys_department_user(企微部门成员关联表)】的数据库操作Service
 * @createDate 2024-04-29 14:46:29
 */
public interface SysDepartmentUserService extends IService<SysDepartmentUser> {

    String findTocken();

    void findDepartmentUser(String access_token,int departmentLength);

    int  findDepartment(String access_token);

    Map<String,String> findUserName(String access_token, String code);
}
