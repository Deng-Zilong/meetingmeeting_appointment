package com.jfzt.meeting.controller;


import com.jfzt.meeting.common.Result;
import com.jfzt.meeting.service.SysDepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
/**
 * 部门表
 *
 * @author ${author}
 * @email ${email}
 * @date 2024-04-28 16:38:52
 */
@CrossOrigin
@RestController
@RequestMapping("department")
public class SysDepartmentController {

    @Autowired
    private SysDepartmentService sysDepartmentService;
    /**
     * 获取企业信息
     * @return
     */
    @GetMapping(value = "department")
    @Transactional
    public Result departmentInfo() {
        //获取部门分组
        sysDepartmentService.findDepartmentAll();

        return null;
    }

}