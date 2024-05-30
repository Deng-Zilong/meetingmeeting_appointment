package com.jfzt.meeting.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jfzt.meeting.common.Result;
import com.jfzt.meeting.entity.SysDepartment;
import com.jfzt.meeting.entity.SysDepartmentUser;
import com.jfzt.meeting.entity.SysUser;
import com.jfzt.meeting.exception.ErrorCodeEnum;
import com.jfzt.meeting.exception.RRException;
import com.jfzt.meeting.mapper.SysDepartmentMapper;
import com.jfzt.meeting.mapper.SysDepartmentUserMapper;
import com.jfzt.meeting.mapper.SysUserMapper;
import com.jfzt.meeting.service.SysDepartmentService;
import com.jfzt.meeting.service.SysDepartmentUserService;
import com.jfzt.meeting.service.SysUserService;
import com.jfzt.meeting.utils.EncryptUtils;
import com.jfzt.meeting.utils.HttpClientUtil;
import jakarta.annotation.Resource;
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

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * @author zilong.deng
 * @description 针对表【sys_department_user(企微部门成员关联表)】的数据库操作Service实现
 * @createDate 2024-04-29 14:46:29
 */
@Service
@Slf4j
public class SysDepartmentUserServiceImpl extends ServiceImpl<SysDepartmentUserMapper, SysDepartmentUser>
        implements SysDepartmentUserService {



    @Autowired
    private SysDepartmentUserMapper sysDepartmentUserMapper;
    @Autowired
    private SysDepartmentMapper sysDepartmentMapper;
    @Autowired
    private WxCpServiceImpl wxCpService;
    @Autowired
    private SysUserMapper sysUserMapper;
    @Resource
    private SysDepartmentUserService sysDepartmentUserService;
    @Resource
    private SysDepartmentService sysDepartmentService;
    @Resource
    private SysUserService sysUserService;



    @Override
    public String findTocken() throws WxErrorException {
        //获取token
        return wxCpService.getAccessToken(true);
    }

    @Override
    public WxCpUser findUserName(String accessToken, String code) throws WxErrorException {
        //获取用户user_ticket
        HttpClientUtil httpClientUtil = new HttpClientUtil();
        HashMap<String, String> tokenCode = new HashMap<>(2);
        tokenCode.put("access_token", accessToken);
        tokenCode.put("code", code);
        String responseAll = httpClientUtil.doGet("https://qyapi.weixin.qq.com/cgi-bin/auth/getuserinfo", tokenCode);
        JSONObject responseAllList = JSONObject.fromObject(responseAll);
        String userid = responseAllList.getString("userid");
        //获取用户详细信息
        WxCpUserServiceImpl wxCpUserService = new WxCpUserServiceImpl(wxCpService);
        return wxCpUserService.getById(userid);
    }

    @Override
    public Long findDepartment() throws WxErrorException {
        WxCpDepartmentServiceImpl wxCpDepartmentService = new WxCpDepartmentServiceImpl(wxCpService);
        List<WxCpDepart> listDepartmentList = wxCpDepartmentService.list(0L);
        List<SysDepartment> sysDepartmentList = sysDepartmentMapper.selectList(null);
        if (sysDepartmentList.size() != 0){
            return (long) sysDepartmentList.size();
        }
        //存入信息
        List<SysDepartment> sysDepartmentLists = new ArrayList<>();
        for (WxCpDepart listDepartment : listDepartmentList) {
            SysDepartment sysDepartment = new SysDepartment();
            sysDepartment.setDepartmentId(listDepartment.getId());
            sysDepartment.setDepartmentName(listDepartment.getName());
            sysDepartment.setParentId(listDepartment.getParentId());
            sysDepartmentLists.add(sysDepartment);
        }
        sysDepartmentMapper.insertAll(sysDepartmentLists);
        return (long) listDepartmentList.size();
    }

    @Override
    public Boolean findDepartmentUser(Long departmentLength) throws WxErrorException, NoSuchAlgorithmException {
        WxCpUserServiceImpl wxCpUserService = new WxCpUserServiceImpl(wxCpService);
        List<WxCpUser> listDepartmentUserList = new ArrayList<>();
        for (int i = 1; i < departmentLength + 1; i++) {
            List<WxCpUser> wxCpUser = wxCpUserService.listSimpleByDepartment((long) i, true, 0);
            listDepartmentUserList.addAll(wxCpUser);
        }
        List<WxCpUser> wxCpUserList = listDepartmentUserList.stream().distinct().toList();
        if (wxCpUserList.size() != 0){
            return true;
        }
        for (WxCpUser wxCpUser : wxCpUserList) {
            SysUser sysUser = new SysUser();
            sysUser.setUserId(wxCpUser.getUserId());
            sysUser.setUserName(wxCpUser.getName());
            sysUser.setPassword(EncryptUtils.encrypt(EncryptUtils.md5encrypt(wxCpUser.getUserId())));
            sysUser.setLevel(wxCpUser.getEnable());
            sysUserMapper.insert(sysUser);
            for (int s = 0; s < wxCpUser.getDepartIds().length; s++) {
                SysDepartmentUser sysDepartmentUser = new SysDepartmentUser();
                sysDepartmentUser.setUserId(wxCpUser.getUserId());
                sysDepartmentUser.setDepartmentId(wxCpUser.getDepartIds()[s]);
                sysDepartmentUserMapper.insert(sysDepartmentUser);
            }
        }
        return true;
    }



    /**
     * @Description 获取部门成员树
     * @Param [id]
     * @return com.jfzt.meeting.common.Result<java.util.List<com.jfzt.meeting.entity.SysDepartment>>
     */
    @Override
    public Result<List<SysDepartment>> gainUsers() {
        // 获取所有部门列表
        List<SysDepartment> departmentList = sysDepartmentService.list();
        // 获取成员树
        List<SysDepartment> departments = departmentList.stream()
                // 过滤顶级部门
                .filter(sysDepartment -> sysDepartment.getParentId() == 5201314)
                // 排序
                .sorted((node1, node2) -> Math.toIntExact(node1.getDepartmentId() - node2.getDepartmentId()))
                // 递归设置子部门
                .peek((topNode) -> topNode.setChildrenPart(getChildren(topNode, departmentList)))
                .toList()
                .getFirst()
                .getChildrenPart();
        return Result.success(departments);
    }

    /**
     * @Description 递归获取子部门
     * @Param SysDepartment root, List<SysDepartment> all,String userName
     * @return List<SysDepartment>
     */
    private List<SysDepartment> getChildren(SysDepartment root, List<SysDepartment> all) {
        return all.stream()
                // 过滤出父部门ID等于根部门ID的部门
                .filter(sysDepartment -> Objects.equals(sysDepartment.getParentId(), root.getDepartmentId()))
                // 对每个子部门进行操作
                .peek(childrenNode -> {
                    // 创建一个用户列表
                    ArrayList<SysUser> sysUsers = new ArrayList<>();
                    // 获取子部门的用户
                    childrenNode.setChildrenPart(getChildren(childrenNode, all));
                    // 获取部门用户
                    List<SysDepartmentUser> departmentUsers = sysDepartmentUserService.lambdaQuery()
                            .eq(SysDepartmentUser::getDepartmentId , childrenNode.getDepartmentId())
                            .list();
                    // 对每个部门用户进行操作
                    departmentUsers.stream().peek(sysDepartmentUser -> {
                        // 根据用户ID获取用户
                        List<SysUser> userList = sysUserService.lambdaQuery()
                                .eq(SysUser::getUserId, sysDepartmentUser.getUserId())
                                .list();
                        // 将用户添加到用户列表中
                        sysUsers.addAll(userList);
                    }).toList();
                    // 设置部门用户的树状结构
                    childrenNode.setTreeUsers(sysUsers);
                }).collect(Collectors.toList());
    }


}




