package com.jfzt.meeting.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.JsonObject;
import com.jfzt.meeting.entity.SysEnterpriseEntity;
import com.jfzt.meeting.entity.SysUserEnterpriseEntity;
import com.jfzt.meeting.mapper.SysUserEnterpriseMapper;
import com.jfzt.meeting.service.SysUserEnterpriseService;
import com.jfzt.meeting.utils.HttpClientUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service("sysUserEnterpriseService")
public class SysUserEnterpriseServiceImpl extends ServiceImpl<SysUserEnterpriseMapper, SysUserEnterpriseEntity> implements SysUserEnterpriseService {

    @Value("${qywx.corpid}")
    private String corpid;
    @Value("${qywx.corpsecret}")
    private String corpsecret;
    @Value("${qywx.agentid}")
    private String agentid;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Override
    public String findTocken() {
        HttpClientUtil httpClientUtil = new HttpClientUtil();
        //身份验证
        HashMap<String, String> mapParams = new HashMap<>();
        mapParams.put("corpid", corpid);
        mapParams.put("corpsecret", corpsecret);
        String responseAll = httpClientUtil.doGet("https://qyapi.weixin.qq.com/cgi-bin/gettoken", mapParams);
        Map<String, String> map = getStringStringMap(responseAll);
        System.out.println("access_token"+map.get("access_token"));
//        stringRedisTemplate.opsForValue().set("access_token",map.get("access_token"),2, TimeUnit.HOURS);
        return map.get("access_token");
    }

    @Override
    public void findUserName(String access_token,String code) {
        HttpClientUtil httpClientUtil = new HttpClientUtil();
        //获取user_ticket
        HashMap<String, String> mapParams1 = new HashMap<>();
        mapParams1.put("access_token", access_token);
        mapParams1.put("code", code);
        String responseAll1 = httpClientUtil.doGet("https://qyapi.weixin.qq.com/cgi-bin/service/auth/getuserinfo3rd", mapParams1);
        Map<String, String> map1 = getStringStringMap(responseAll1);
        System.out.println(map1);

        //身份验证,获取用户详细信息
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("user_ticket", map1.get("user_ticket"));
        String responseAll = httpClientUtil.doPost("https://qyapi.weixin.qq.com/cgi-bin/service/auth/getuserdetail3rd?suite_access_token="+access_token, jsonObject);
        Map<String, String> map = getStringStringMap(responseAll);
        System.out.println(map);
        SysEnterpriseEntity sysEnterpriseEntity = new SysEnterpriseEntity();


    }



    @Override
    public String findQrCode() {
        HttpClientUtil httpClientUtil = new HttpClientUtil();
        //身份验证
        HashMap<String, String> mapParams = new HashMap<>();
        mapParams.put("login_type", "CorpApp");
        mapParams.put("appid", corpid);
        mapParams.put("agentid", agentid);
        mapParams.put("redirect_uri", "http%3A%2F%2Fggssyy.cn%2Flogin%2Fuser");
        mapParams.put("state", "WWLogin");
        String responseAll = httpClientUtil.doGet("https://login.work.weixin.qq.com/wwlogin/sso/login", mapParams);
       return responseAll;
    }

    @Override
    public void findDepartment(String access_token, String code) {
        HttpClientUtil httpClientUtil = new HttpClientUtil();
        //获取user_ticket
        HashMap<String, String> mapParams = new HashMap<>();
        mapParams.put("access_token", access_token);
        mapParams.put("department_id", "1");
        String responseAll1 = httpClientUtil.doGet("https://qyapi.weixin.qq.com/cgi-bin/user/simplelist", mapParams);
        Map<String, String> map1 = getStringStringMap(responseAll1);
        System.out.println(map1);
    }

    /**
     * String的key,value 转化成Map类型
     * @param responseAll
     * @return
     */
    private Map<String, String> getStringStringMap(String responseAll) {
        return Stream.of(responseAll.split(","))
                .map(entry -> entry.split("="))
                .collect(Collectors.toMap(
                        entry -> entry[0],
                        entry -> entry.length > 1 ? entry[1] : ""
                ));
    }

}