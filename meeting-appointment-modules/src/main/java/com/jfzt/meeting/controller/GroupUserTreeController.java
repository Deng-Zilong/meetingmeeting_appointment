package com.jfzt.meeting.controller;
import com.jfzt.meeting.common.Result;
import com.jfzt.meeting.entity.SysDepartment;
import com.jfzt.meeting.service.SysDepartmentUserService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 查询所有部门下成员，返回前端树形成员图
 * @Author: chenyu.di
 * @since: 2024-05-07 15:02
 */
@RestController
@RequestMapping("/meeting/groupUserTree")
public class GroupUserTreeController {

    @Resource
    private SysDepartmentUserService sysDepartmentUserService;

    /**
     * @Description 获取部门成员树
     * @Param []
     * @return com.jfzt.meeting.common.Result<java.util.List<com.jfzt.meeting.entity.SysDepartment>>
     * @exception
     */
    @GetMapping("/getGroupUserTree")
    private Result<List<SysDepartment>> getGroupUserTree(){
        return sysDepartmentUserService.gainUsers();
    }

}