package com.jfzt.meeting.controller;

import com.jfzt.meeting.common.Result;
import com.jfzt.meeting.entity.SysDepartment;
import com.jfzt.meeting.entity.vo.SysUserVO;
import com.jfzt.meeting.service.SysDepartmentUserService;
import com.jfzt.meeting.service.SysUserService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 查询所有部门下成员，返回前端树形成员图
 * @author chenyu.di
 * @since 2024-05-07 15:02
 */
@RestController
@RequestMapping("/meeting/tree")
public class GroupUserTreeController {

    @Resource
    private SysDepartmentUserService sysDepartmentUserService;
    @Resource
    private SysUserService sysUserService;

    /**
     *  获取部门成员树
     * @return 成员树
     */
    @GetMapping("/group-user-tree")
    public Result<List<SysDepartment>> getGroupUserTree () {
        return sysDepartmentUserService.gainUsers();
    }

    /**
     *  模糊查询成员
     * @param name 姓名
     * @return 成员VO
     */
    @GetMapping("/like-by-name")
    public Result<List<SysUserVO>> likeByName (@RequestParam String name) {
        return sysUserService.findByName(name);
    }

}