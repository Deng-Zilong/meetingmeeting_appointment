package com.jfzt.meeting.controller;

import com.jfzt.meeting.common.Result;
import com.jfzt.meeting.entity.MeetingNotice;
import com.jfzt.meeting.service.MeetingNoticeService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import static com.jfzt.meeting.service.impl.SysUserServiceImpl.ADMIN_LEVEL;
import static com.jfzt.meeting.service.impl.SysUserServiceImpl.SUPER_ADMIN_LEVEL;

/**
 * 会议群组控制类
 *
 * @Author: chenyu.di
 * @since: 2024-04-29 16:44
 */
@CrossOrigin
@RestController
@RequestMapping("/meetingNotice")
public class MeetingNoticeController {

    @Resource
    private MeetingNoticeService meetingNoticeService;

    /**
     * 新增通告信息
     * @param meetingNotice
     * @return
     */
    @PostMapping("/addNotice")
    public Result<Object> addNotice(@RequestBody MeetingNotice meetingNotice) {
        // 获取当前登录用户的权限等级
        //Integer level = BaseContext.getCurrentLevel();
        Integer level = 1;
        if (SUPER_ADMIN_LEVEL.equals(level) || ADMIN_LEVEL.equals(level)){
            int row = meetingNoticeService.addNotice(meetingNotice);
            if (row > 0){
                return Result.success("新增成功!");
            }else {
                return Result.fail("新增失败!");
            }
        }
        return Result.fail("新增失败，请联系管理员获取权限！");
    }

    /**
     * 删除通告
     * @param id
     * @return
     */
    @DeleteMapping("/deleteNotice/{id}")
    public Result<Object> deleteMeetingGroup(@PathVariable("id") Long id) {
        return Result.success(meetingNoticeService.deleteNotice(id));
    }

    /**
     * 查询所有公告
     * @param meetingNotice
     * @return
     */
    @GetMapping("/getNotice")
    public Result<List<String>> getNotice(MeetingNotice meetingNotice){
        List<String> stringList = meetingNoticeService.selectAll(meetingNotice);
        if (!stringList.isEmpty()){
            return Result.success(stringList);
        }
        return Result.success(null);
    }
}