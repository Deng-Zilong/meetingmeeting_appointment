package com.jfzt.meeting.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jfzt.meeting.config.WxCpDefaultConfiguration;
import com.jfzt.meeting.entity.SysDepartmentUser;
import com.jfzt.meeting.entity.SysUser;
import com.jfzt.meeting.mapper.SysDepartmentUserMapper;
import com.jfzt.meeting.mapper.SysUserMapper;
import com.jfzt.meeting.utils.EncryptUtils;
import com.jfzt.meeting.utils.xml.CallBackParam;
import com.jfzt.meeting.weixin.AesException;
import com.jfzt.meeting.weixin.WXBizMsgCrypt;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.api.impl.WxCpServiceImpl;
import me.chanjar.weixin.cp.api.impl.WxCpUserServiceImpl;
import me.chanjar.weixin.cp.bean.WxCpUser;
import me.chanjar.weixin.cp.bean.message.WxCpMessage;
import me.chanjar.weixin.cp.bean.message.WxCpMessageSendResult;
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
    private WxCpServiceImpl wxCpService;
    @Autowired
    protected WxCpService wxService;

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
        String sEncodingAESKey = wxCpDefaultConfiguration.getEncodingAESKey();
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
        String sEncodingAESKey = wxCpDefaultConfiguration.getEncodingAESKey();
        WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(sToken, sEncodingAESKey, sCorpID);
        try {
            String sMsg = wxcpt.DecryptMsg(msgSignature, timestamp, nonce, sReqData);
            log.info("after decrypt msg: " + sMsg);
            CallBackParam callBackParam = CallBackParam.fromXml(sMsg);
            String changeType = callBackParam.getAllFieldsMap().get("ChangeType").toString();
            switch (changeType){
                case "create_user":
                    log.info("新增用户");
                    WxCpUserServiceImpl wxCpUserService = new WxCpUserServiceImpl(wxCpService);
                    WxCpUser wxCpUser = wxCpUserService.getById(callBackParam.getAllFieldsMap().get("UserID").toString());
                    log.info("11111111111111111111111111111111"+wxCpUser);
                    SysUser sysUser = new SysUser();
                    sysUser.setUserId(callBackParam.getAllFieldsMap().get("UserID").toString());
                    sysUser.setUserName(wxCpUser.getName());
                    sysUser.setLevel(3);
                    sysUser.setPassword(EncryptUtils.encrypt("Aa111111"));
                    sysUserMapper.insert(sysUser);
                    SysDepartmentUser sysDepartmentUser = new SysDepartmentUser();
                    sysDepartmentUser.setUserId(callBackParam.getAllFieldsMap().get("UserID").toString());
                    sysDepartmentUser.setDepartmentId(Long.parseLong(callBackParam.getAllFieldsMap().get("Department").toString()));
                    sysDepartmentUserMapper.insert(sysDepartmentUser);
                    break;
                case "delete_user":
                    log.info("删除用户");
                    LambdaQueryWrapper<SysUser> sysUserLambdaQueryWrapper1 = new LambdaQueryWrapper<>();
                    sysUserLambdaQueryWrapper1.eq(SysUser::getUserId,callBackParam.getAllFieldsMap().get("UserID").toString());
                    sysUserMapper.delete(sysUserLambdaQueryWrapper1);
                    LambdaQueryWrapper<SysDepartmentUser> sysDepartmentUserLambdaQueryWrapper = new LambdaQueryWrapper<>();
                    sysDepartmentUserLambdaQueryWrapper.eq(SysDepartmentUser::getUserId,callBackParam.getAllFieldsMap().get("UserID").toString());
                    sysDepartmentUserMapper.delete(sysDepartmentUserLambdaQueryWrapper);
                    break;
                default:
                    log.info("其他用户操作");
                    break;
            }
            log.info("CallBackParam json \n{}", JSON.toJSONString(callBackParam, SerializerFeature.PrettyFormat));
        } catch (Exception e) {
            // TODO
            // 解密失败，失败原因请查看异常
            log.error("Exception", e);
        }
    }
    @GetMapping("/test")
    public Boolean test() throws WxErrorException {

        WxCpMessage message = WxCpMessage
                .MARKDOWN()
                .toUser("ZhaiChenYu|GuLi|d|QianRuoXiaMo|XingChen|")
                .content("您的会议室已经预定，稍后会同步到`邮箱` \n" +
                        "                >**事项详情** \n" +
                        "                >事　项：开会\n" +
                        "                >组织者：@miglioguan \n" +
                        "                >参与者：@miglioguan、@kunliu、@jamdeezhou、@kanexiong、@kisonwang \n" +
                        "                > \n" +
                        "                >会议室：广州TIT 1楼 301\n" +
                        "                >日　期：2018年5月18日\n" +
                        "                >时　间：上午9:00-11:00\n" +
                        "                > \n" +
                        "                >请准时参加会议。 \n" +
                        "                > \n" +
                        "                >如需修改会议信息，请点击：[修改会议信息](https://work.weixin.qq.com)")
                .build();

        WxCpMessageSendResult messageSendResult = this.wxService.getMessageService().send(message);

        return true;
    }

}
