package com.jfzt.meeting.controller;
import com.jfzt.meeting.common.Result;
import com.jfzt.meeting.service.SysDepartmentUserService;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("login")
public class SysUserController {

    @Autowired
    private SysDepartmentUserService sysDepartmentUserService;



    /**
     * 获取企业信息
     * @param code
     * @return
     */
    @GetMapping(value = "logininfo")
    @Transactional
    public Result login(@RequestParam("code") String code) throws JSONException {
        //获取登录tocken
        String access_token = sysDepartmentUserService.findTocken();
        //获取部门信息
        int departmentLength =  sysDepartmentUserService.findDepartment(access_token);
        //获取企业部门用户
        sysDepartmentUserService.findDepartmentUser(access_token,departmentLength);
        return null;
    }
}
