package com.jfzt.meeting.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jfzt.meeting.entity.SysDepartment;
import com.jfzt.meeting.mapper.SysDepartmentMapper;
import com.jfzt.meeting.service.SysDepartmentService;
import com.jfzt.meeting.utils.HttpClientUtil;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author zilong.deng
 * @description 针对表【sys_department】的数据库操作Service实现
 * @createDate 2024-04-28 16:04:11
 */
@Service
public class SysDepartmentServiceImpl extends ServiceImpl<SysDepartmentMapper, SysDepartment>
        implements SysDepartmentService {

    @Override
    public void findDepartmentAll() {
        HttpClientUtil httpClientUtil = new HttpClientUtil();
        //身份验证
        HashMap<String, String> mapParams = new HashMap<>();
        mapParams.put("access_token", "access_token");
        String responseAll = httpClientUtil.doGet("https://qyapi.weixin.qq.com/cgi-bin/department/list", mapParams);
        Map<String, String> map = getStringStringMap(responseAll);
        System.out.println(map);
    }

    /**
     * String的key,value 转化成Map类型
     * @param responseAll
     * @return
     */
    private Map<String, String> getStringStringMap(String responseAll) {
        return  Stream.of(responseAll.replace("{","").replace("}","").split(","))
                .map(entry -> entry.split(":"))
                .collect(Collectors.toMap(
                        entry -> entry[0],
                        entry -> entry.length > 1 ? entry[1] : ""
                ));
    }
}




