package com.jfzt.meeting.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jfzt.meeting.entity.SysDepartmentUser;
import com.jfzt.meeting.mapper.SysDepartmentUserMapper;
import com.jfzt.meeting.service.SysDepartmentUserService;
import com.jfzt.meeting.utils.HttpClientUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author zilong.deng
 * @description 针对表【sys_department_user(企微部门成员关联表)】的数据库操作Service实现
 * @createDate 2024-04-29 14:46:29
 */
@Service
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

    @Override
    public String findTocken () {
        HttpClientUtil httpClientUtil = new HttpClientUtil();
        //身份验证
        HashMap<String, String> mapParams = new HashMap<>();
        mapParams.put("corpid", corpid);
        mapParams.put("corpsecret", corpsecret);
        String responseAll = httpClientUtil.doGet("https://qyapi.weixin.qq.com/cgi-bin/gettoken", mapParams);
        Map<String, String> map = getStringStringMap(responseAll);
        System.out.println("access_token" + map.get("access_token"));
        //        stringRedisTemplate.opsForValue().set("access_token",map.get("access_token"),2, TimeUnit.HOURS);
        return map.get("access_token");
    }

    @Override
    public void findDepartment (String access_token, String code) {
        HttpClientUtil httpClientUtil = new HttpClientUtil();
        HashMap<String, String> mapParams = new HashMap<>();
        mapParams.put("access_token", access_token);
        mapParams.put("department_id", "1");
        String responseAll1 = httpClientUtil.doGet("https://qyapi.weixin.qq.com/cgi-bin/user/simplelist", mapParams);
        Map<String, String> map1 = getStringStringMap(responseAll1);
        System.out.println(map1);
    }

    /**
     * String的key,value 转化成Map类型
     *
     * @param responseAll
     * @return
     */
    private Map<String, String> getStringStringMap (String responseAll) {
        return Stream.of(responseAll.replace("{", "").replace("}", "").split(","))
                .map(entry -> entry.split(":"))
                .collect(Collectors.toMap(
                        entry -> entry[0],
                        entry -> entry.length > 1 ? entry[1] : ""
                ));
    }
}




