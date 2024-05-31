package com.jfzt.meeting.utils;

import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.bean.message.WxCpMessage;
import me.chanjar.weixin.cp.bean.message.WxCpMessageSendResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author zilong.deng
 * @date 2024/05/29
 */
@Slf4j
@Component
public class WxUtil {

    @Autowired
    private WxCpService wxService;

    /**
     * 通过企微发送提醒
     *
     * @param reminders 提醒
     * @param userIds   用户id
     */
    public void sendsWxReminders (List<String> userIds, String reminders) throws WxErrorException {
        String userString = String.join("|", userIds);
        log.info("通过企微给用户{}发送会议提醒/n{}", userString, reminders);
        WxCpMessage message = WxCpMessage
                .MARKDOWN()
                .toUser(userString)
                .content(reminders)
                .build();
        WxCpMessageSendResult messageSendResult = wxService.getMessageService().send(message);
        log.info("发送结果:{}", messageSendResult);
    }

}
