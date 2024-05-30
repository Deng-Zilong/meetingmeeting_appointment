package com.jfzt.meeting.controller;
import com.jfzt.meeting.common.Result;
import com.jfzt.meeting.entity.SysDepartment;
import com.jfzt.meeting.entity.SysUser;
import com.jfzt.meeting.entity.vo.SysUserVO;
import com.jfzt.meeting.service.SysDepartmentUserService;
import com.jfzt.meeting.service.SysUserService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 查询所有部门下成员，返回前端树形成员图
 * @Author: chenyu.di
 * @since: 2024-05-07 15:02
 */

@RestController
@RequestMapping("/meeting/tree")
public class GroupUserTreeController {

    @Resource
    private SysDepartmentUserService sysDepartmentUserService;
    @Resource
    private SysUserService sysUserService;

    /**
     * @Description 获取部门成员树
     * @Param [userName]
     * @return com.jfzt.meeting.common.Result<java.util.List<com.jfzt.meeting.entity.SysDepartment>>
     */
    @GetMapping("/group-user-tree")
    public Result<List<SysDepartment>> getGroupUserTree(){
        return sysDepartmentUserService.gainUsers();
    }

    /**
     * @Description 模糊查询成员
     * @Param [name]
     * @return com.jfzt.meeting.common.Result<java.util.List<com.jfzt.meeting.entity.vo.SysUserVO>>
     */
    @GetMapping("/like-by-name")
    public Result<List<SysUserVO>> likeByName (@RequestParam String name){
        return sysUserService.findByName(name);
    }

}