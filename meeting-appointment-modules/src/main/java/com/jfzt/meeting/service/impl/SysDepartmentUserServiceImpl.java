package com.jfzt.meeting.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jfzt.meeting.entity.SysDepartment;
import com.jfzt.meeting.entity.SysDepartmentUser;
import com.jfzt.meeting.mapper.SysDepartmentMapper;
import com.jfzt.meeting.mapper.SysDepartmentUserMapper;
import com.jfzt.meeting.service.SysDepartmentUserService;
import com.jfzt.meeting.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.cp.api.impl.WxCpDepartmentServiceImpl;
import me.chanjar.weixin.cp.api.impl.WxCpServiceImpl;
import me.chanjar.weixin.cp.api.impl.WxCpUserServiceImpl;
import me.chanjar.weixin.cp.bean.WxCpDepart;
import me.chanjar.weixin.cp.bean.WxCpUser;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;


/**
 * @author zilong.deng
 * @description 针对表【sys_department_user(企微部门成员关联表)】的数据库操作Service实现
 * @createDate 2024-04-29 14:46:29
 */
@Service
@Slf4j
public class SysDepartmentUserServiceImpl extends ServiceImpl<SysDepartmentUserMapper, SysDepartmentUser>
        implements SysDepartmentUserService {


    //    @Autowired
    //    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private SysDepartmentUserMapper sysDepartmentUserMapper;
    @Autowired
    private SysDepartmentMapper sysDepartmentMapper;
    @Autowired(required = false)
    private WxCpServiceImpl wxCpService;


    @Override
    public String findTocken () throws WxErrorException {
        //获取token
        return wxCpService.getAccessToken(true);
    }

    @Override
    public WxCpUser findUserName (String accessToken, String code) throws WxErrorException {
        //获取用户user_ticket
        HttpClientUtil httpClientUtil = new HttpClientUtil();
        HashMap<String, String> tokenCode = new HashMap<>(2);
        tokenCode.put("access_token", accessToken);
        //        tokenCode.put("code", code);
        String responseAll = httpClientUtil.doGet("https://qyapi.weixin.qq.com/cgi-bin/auth/getuserinfo", tokenCode);
        JSONObject responseAllList = JSONObject.fromObject(responseAll);
        String userid = responseAllList.getString("userid");
        //获取用户详细信息
        WxCpUserServiceImpl wxCpUserService = new WxCpUserServiceImpl(wxCpService);
        return wxCpUserService.getById(userid);
    }

    @Override
    public Long findDepartment () throws WxErrorException {
        WxCpDepartmentServiceImpl wxCpDepartmentService = new WxCpDepartmentServiceImpl(wxCpService);
        List<WxCpDepart> listDepartmentList = wxCpDepartmentService.list(0L);
        //存入信息
        for (WxCpDepart listDepartment : listDepartmentList) {
            SysDepartment sysDepartment = new SysDepartment();
            sysDepartment.setDepartmentId(listDepartment.getId());
            sysDepartment.setDepartmentName(listDepartment.getName());
            sysDepartment.setParentId(listDepartment.getParentId());
            sysDepartmentMapper.insert(sysDepartment);
        }
        return (long) listDepartmentList.size();
    }

    @Override
    public void findDepartmentUser (Long departmentLength) throws WxErrorException {
        WxCpUserServiceImpl wxCpUserService = new WxCpUserServiceImpl(wxCpService);
        for (int i = 0; i < departmentLength; i++) {
            List<WxCpUser> listDepartmentUserList = wxCpUserService.listSimpleByDepartment((long) i, true, 0);
            for (WxCpUser departmentUser : listDepartmentUserList) {
                SysDepartmentUser sysDepartmentUser = new SysDepartmentUser();
                for (int s = 0; s < departmentUser.getDepartIds().length; i++) {
                    sysDepartmentUser.setDepartmentId(departmentUser.getDepartIds()[s]);
                    sysDepartmentUser.setUserId(departmentUser.getUserId());
                    sysDepartmentUser.setUserName(departmentUser.getName());
                    sysDepartmentUserMapper.insert(sysDepartmentUser);
                }
            }

        }

    }


}




