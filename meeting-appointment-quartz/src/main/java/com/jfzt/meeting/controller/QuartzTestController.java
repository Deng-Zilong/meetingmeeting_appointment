package com.jfzt.meeting.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jfzt.meeting.config.WxCpDefaultConfiguration;
import com.jfzt.meeting.entity.*;
import com.jfzt.meeting.mapper.*;
import com.jfzt.meeting.utils.EncryptUtils;
import com.jfzt.meeting.utils.xml.CallBackParam;
import com.jfzt.meeting.weixin.AesException;
import com.jfzt.meeting.weixin.WXBizMsgCrypt;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.api.impl.WxCpServiceImpl;
import me.chanjar.weixin.cp.api.impl.WxCpUserServiceImpl;
import me.chanjar.weixin.cp.bean.WxCpUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/** 监听企业微信人员变动，和获取企业用户变动信息
 * @author zhenxing.lu
 * @date 2024/04/25
 */
@Slf4j
@RestController
@RequestMapping("/quartz")
public class QuartzTestController {

    @Autowired
    private WxCpDefaultConfiguration wxCpDefaultConfiguration;

    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private SysDepartmentUserMapper sysDepartmentUserMapper;
    @Autowired
    private UserGroupMapper userGroupMapper;
    @Autowired
    private MeetingAttendeesMapper meetingAttendeesMapper;
    @Autowired
    private WxCpServiceImpl wxCpService;
    @Autowired
    protected WxCpService wxService;
    @Autowired
    private MeetingGroupMapper meetingGroupMapper;
    @Autowired
    private MeetingMinutesMapper meetingMinutesMapper;
    @Autowired
    private MeetingNoticeMapper meetingNoticeMapper;
    @Autowired
    private MeetingRecordMapper recordMapper;

    /**
     * 设置接收事件服务器
     * @author zhenxing.lu
     */
    @GetMapping("/receiving-users")
    public String validApi(@RequestParam(name = "msg_signature") String msgSignature,
                           @RequestParam(name = "timestamp") String timestamp,
                           @RequestParam(name = "nonce") String nonce,
                           @RequestParam(name = "echostr") String sVerifyEchoStr) throws AesException {
        String sToken = wxCpDefaultConfiguration.getToken();
        String sCorpID = wxCpDefaultConfiguration.getCorpid();
        String sEncodingAESKey = wxCpDefaultConfiguration.getEncodingAesKey();
        String sEchoStr; //需要返回的明文
        WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(sToken, sEncodingAESKey, sCorpID);
        try {
            sEchoStr = wxcpt.VerifyURL(msgSignature, String.valueOf(timestamp),
                    nonce, sVerifyEchoStr);
            log.info("verifyurlechostr: " + sEchoStr);
            // 验证URL成功，将sEchoStr返回
            return sEchoStr;
        } catch (Exception e) {
            //验证URL失败，错误原因请查看异常
            log.error("Exception", e);
        }
        return "error";
    }

    @PostMapping("/receiving-users")
    public void receive(@RequestParam(name = "msg_signature") String msgSignature,
                        @RequestParam(name = "timestamp") String timestamp,
                        @RequestParam(name = "nonce") String nonce,
                        @RequestBody String sReqData) throws AesException {

        log.info("msg_signature = {} \n timestamp = {} \n nonce = {} \n sReqData = {}", msgSignature, timestamp, nonce, sReqData);

        String sToken = wxCpDefaultConfiguration.getToken();
        String sCorpID = wxCpDefaultConfiguration.getCorpid();
        String sEncodingAESKey = wxCpDefaultConfiguration.getEncodingAesKey();
        WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(sToken, sEncodingAESKey, sCorpID);
        try {
            String sMsg = wxcpt.DecryptMsg(msgSignature, timestamp, nonce, sReqData);
            log.info("after decrypt msg: " + sMsg);
            CallBackParam callBackParam = CallBackParam.fromXml(sMsg);
            String changeType = callBackParam.getAllFieldsMap().get("ChangeType").toString();
            switch (changeType) {
                case "create_user" -> {
                    log.info("新增用户");
                    WxCpUserServiceImpl wxCpUserService = new WxCpUserServiceImpl(wxCpService);
                    WxCpUser wxCpUser = wxCpUserService.getById(callBackParam.getAllFieldsMap().get("UserID").toString());
                    SysUser sysUser = new SysUser();
                    sysUser.setUserId(callBackParam.getAllFieldsMap().get("UserID").toString());
                    sysUser.setUserName(wxCpUser.getName());
                    sysUser.setLevel(2);
                    sysUser.setPassword(EncryptUtils.encrypt(EncryptUtils.md5encrypt("Aa111111")));
                    sysUserMapper.insert(sysUser);
                    SysDepartmentUser sysDepartmentUser = new SysDepartmentUser();
                    sysDepartmentUser.setUserId(callBackParam.getAllFieldsMap().get("UserID").toString());
                    sysDepartmentUser.setDepartmentId(Long.parseLong(callBackParam.getAllFieldsMap().get("Department").toString()));
                    sysDepartmentUserMapper.insert(sysDepartmentUser);
                }
                case "delete_user" -> {
                    log.info("删除用户");
                    meetingAttendeesMapper.delete(new LambdaQueryWrapper<MeetingAttendees>().
                            eq(MeetingAttendees::getUserId, callBackParam.getAllFieldsMap().get("UserID").toString()));
                    meetingGroupMapper.delete(new LambdaQueryWrapper<MeetingGroup>().
                            eq(MeetingGroup::getCreatedBy, callBackParam.getAllFieldsMap().get("UserID").toString()));
                    meetingMinutesMapper.delete(new LambdaQueryWrapper<MeetingMinutes>().
                            eq(MeetingMinutes::getUserId, callBackParam.getAllFieldsMap().get("UserID").toString()));
                    meetingNoticeMapper.delete(new LambdaQueryWrapper<MeetingNotice>().
                            eq(MeetingNotice::getUserId, callBackParam.getAllFieldsMap().get("UserID").toString()));
                    recordMapper.delete(new LambdaQueryWrapper<MeetingRecord>().
                            eq(MeetingRecord::getCreatedBy, callBackParam.getAllFieldsMap().get("UserID").toString()));
                    sysDepartmentUserMapper.delete(new LambdaQueryWrapper<SysDepartmentUser>().
                            eq(SysDepartmentUser::getUserId, callBackParam.getAllFieldsMap().get("UserID").toString()));
                    sysUserMapper.delete(new LambdaQueryWrapper<SysUser>().
                            eq(SysUser::getUserId, callBackParam.getAllFieldsMap().get("UserID").toString()));
                    userGroupMapper.delete(new LambdaQueryWrapper<UserGroup>().
                            eq(UserGroup::getUserId, callBackParam.getAllFieldsMap().get("UserID").toString()));
                }
                default -> log.info("其他用户操作");
            }
            log.info("CallBackParam json \n{}", JSON.toJSONString(callBackParam, SerializerFeature.PrettyFormat));
        } catch (Exception e) {
            // TODO
            // 解密失败，失败原因请查看异常
            log.error("Exception", e);
        }
    }

}
