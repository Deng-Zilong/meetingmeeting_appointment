package com.jfzt.meeting.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jfzt.meeting.entity.SysDepartment;
import com.jfzt.meeting.entity.SysDepartmentUser;
import com.jfzt.meeting.mapper.SysDepartmentMapper;
import com.jfzt.meeting.mapper.SysDepartmentUserMapper;
import com.jfzt.meeting.service.SysDepartmentUserService;
import com.jfzt.meeting.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zilong.deng
 * @description 针对表【sys_department_user(企微部门成员关联表)】的数据库操作Service实现
 * @createDate 2024-04-29 14:46:29
 */
@Service
@Slf4j
public class SysDepartmentUserServiceImpl extends ServiceImpl<SysDepartmentUserMapper, SysDepartmentUser>
        implements SysDepartmentUserService {

    @Value("${qywx.corpid}")
    private String corpid;
    @Value("${qywx.corpsecret}")
    private String corpsecret;
    @Value("${qywx.agentid}")
    private String agentid;
//    @Autowired
//    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private SysDepartmentUserMapper sysDepartmentUserMapper;
    @Autowired
    private SysDepartmentMapper sysDepartmentMapper;

    @Override
    public String findTocken() {
        //获取token
        HttpClientUtil httpClientUtil = new HttpClientUtil();
        HashMap<String, String> mapParams = new HashMap<>();
        mapParams.put("corpid", corpid);
        mapParams.put("corpsecret", corpsecret);
        String responseAll = httpClientUtil.doGet("https://qyapi.weixin.qq.com/cgi-bin/gettoken", mapParams);
        JSONObject responseAllList = JSONObject.fromObject(responseAll);
        String access_token = (String) responseAllList.get("access_token");
//        stringRedisTemplate.opsForValue().set("access_token",map.get("access_token"),2, TimeUnit.HOURS);
        return access_token;
    }

    @Override
    public void findDepartmentUser(String access_token,int departmentLength) {
        HttpClientUtil httpClientUtil = new HttpClientUtil();
        for (int s=0;s<departmentLength;s++){
            HashMap<String, String> mapParams = new HashMap<>();
            mapParams.put("access_token", access_token);
            mapParams.put("department_id", String.valueOf(s));
            String responseAll = httpClientUtil.doGet("https://qyapi.weixin.qq.com/cgi-bin/user/simplelist", mapParams);
            JSONObject responseAllList = JSONObject.fromObject(responseAll);
            JSONArray userList = responseAllList.getJSONArray("userlist");
            for (int i = 0; i < userList.size(); i++) {
                JSONArray departmentList = userList.getJSONObject(i).getJSONArray("department");
                for (int j = 0; j < departmentList.size(); j++) {
                    SysDepartmentUser sysDepartmentUser = new SysDepartmentUser();
                    JSONObject info = userList.getJSONObject(i);
                    sysDepartmentUser.setUserId(info.getString("userid"));
                    sysDepartmentUser.setUserName(info.getString("name"));
                    sysDepartmentUser.setDepartmentId(Long.valueOf(departmentList.getString(j)));
                    sysDepartmentUserMapper.insert(sysDepartmentUser);
                }
            }
        }

    }

    @Override
    public int  findDepartment(String access_token) {
        HttpClientUtil httpClientUtil = new HttpClientUtil();
        HashMap<String, String> mapParams = new HashMap<>();
        mapParams.put("access_token", access_token);
        String responseAll = httpClientUtil.doGet("https://qyapi.weixin.qq.com/cgi-bin/department/list", mapParams);
        JSONObject responseAllList = JSONObject.fromObject(responseAll);
        JSONArray userList = responseAllList.getJSONArray("department");
        for (int i = 0; i < userList.size(); i++) {
            SysDepartment sysDepartment = new SysDepartment();
            JSONObject info = userList.getJSONObject(i);
            sysDepartment.setDepartmentId(Long.valueOf(info.getString("id")));
            sysDepartment.setDepartmentName(info.getString("name"));
            sysDepartment.setParentId(Long.valueOf(info.getString("parentid")));
            sysDepartmentMapper.insert(sysDepartment);
        }
        int length = userList.size();
        return length;
    }

    @Override
    public Map<String,String> findUserName(String access_token, String code) {
        Map<String,String> userInfo = new HashMap<>();
        //获取用户user_ticket
        HttpClientUtil httpClientUtil = new HttpClientUtil();
        HashMap<String, String> tokenCode = new HashMap<>();
        tokenCode.put("access_token", access_token);
        tokenCode.put("code", code);
        String responseAll = httpClientUtil.doGet("https://qyapi.weixin.qq.com/cgi-bin/auth/getuserinfo", tokenCode);
        JSONObject responseAllList = JSONObject.fromObject(responseAll);
        String userid = responseAllList.getString("userid");
        log.info("userid获取"+userid);
        //获取用户详细信息
        HashMap<String, String> tokenUserid = new HashMap<>();
        tokenUserid.put("access_token", access_token);
        tokenUserid.put("userid", userid);
        String responseAllS = httpClientUtil.doGet("https://qyapi.weixin.qq.com/cgi-bin/user/get", tokenUserid);
        JSONObject responseAllListS = JSONObject.fromObject(responseAllS);
        userInfo.put("userId",responseAllListS.getString("userId"));
        userInfo.put("username",responseAllListS.getString("name"));
        return userInfo;
    }

}




