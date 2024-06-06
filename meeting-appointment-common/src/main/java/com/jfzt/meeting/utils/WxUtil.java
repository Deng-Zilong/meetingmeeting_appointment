package com.jfzt.meeting.utils;

import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.bean.article.MpnewsArticle;
import me.chanjar.weixin.cp.bean.message.WxCpMessage;
import me.chanjar.weixin.cp.bean.message.WxCpMessageSendResult;
import me.chanjar.weixin.cp.bean.templatecard.HorizontalContent;
import me.chanjar.weixin.cp.bean.templatecard.TemplateCardButton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 企业微信信息提示工具类
 * @author zilong.deng
 * @since 2024-5-29 10:12:12
 */
@Slf4j
@Component
public class WxUtil {

    @Autowired
    private WxCpService wxService;

    /**
     * 通过企微发送提醒
     * @param reminders 提醒
     * @param userIds   用户id
     */
    public void sendsWxReminders (List<String> userIds, String reminders){
        String userString = String.join("|", userIds);
        log.info("通过企微给用户{}发送会议提醒/n{}", userString, reminders);
        WxCpMessage message = WxCpMessage
                .MARKDOWN()
                .toUser(userString)
                .content(reminders)
                .build();
        WxCpMessageSendResult messageSendResult = null;
        try {
            messageSendResult = wxService.getMessageService().send(message);
        } catch (WxErrorException e) {
            log.error("发送消息失败:{}", e.getMessage());
        }
        log.info("发送结果:{}", messageSendResult);
    }
    public void sendMiniProgramNotice (List<String> userIds, String reminders) throws WxErrorException {
        String userString = String.join("|", userIds);
        log.info("通过企微给用户{}发送会议提醒/n{}", userString, reminders);
        HashMap<String, String> contentItems = new HashMap<>();
        contentItems.put("会议室", "402");
        contentItems.put("会议地点", "广州TIT-402会议室");
        contentItems.put("会议时间", "2018年8月1日 09:00-09:30");
        WxCpMessage message = WxCpMessage
                .newMiniProgramNoticeBuilder()
                .toUser(userString)
                .title("会议室预订成功通知")
                .description(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .emphasisFirstItem(true)
                .contentItems(contentItems)
                .build();
        WxCpMessageSendResult messageSendResult = wxService.getMessageService().send(message);
        log.info("发送结果:{}", messageSendResult);
    }
    public void sendMpNews (List<String> userIds, String reminders) throws WxErrorException {
        String userString = String.join("|", userIds);
        log.info("通过企微给用户{}发送会议提醒/n{}", userString, reminders);
        MpnewsArticle mpnewsArticle = getMpnewsArticle();
        WxCpMessage message = WxCpMessage.MPNEWS().addArticle(mpnewsArticle)
                .toUser(userString).build();
        WxCpMessageSendResult messageSendResult = wxService.getMessageService().send(message);
        log.info("发送结果:{}", messageSendResult);
    }

    private static MpnewsArticle getMpnewsArticle () {
        MpnewsArticle mpnewsArticle = new MpnewsArticle();
        mpnewsArticle.setContentSourceUrl("https://cn.bing.com/images");
        mpnewsArticle.setTitle("会议室预订成功通知");
        mpnewsArticle.setContent("会议室预订成功通知");
        return mpnewsArticle;
    }

    /**
     * 按钮交互型卡片
     **/
    public void sendTemplateCard (List<String> userIds, String reminders) throws WxErrorException {
        String userString = String.join("|", userIds);
        ArrayList<HorizontalContent> content = new ArrayList<>();
        HorizontalContent horizontalContent = new HorizontalContent();
        horizontalContent.setKeyname("会议室");
        horizontalContent.setValue("402");
        content.add(horizontalContent);
        List<TemplateCardButton> buttonsList = new ArrayList<>();
        TemplateCardButton button1 = new TemplateCardButton();
        button1.setKey("cancel");
        button1.setText("取消");
        TemplateCardButton button2 = new TemplateCardButton();
        button2.setKey("confirm");
        button2.setText("确认");
        buttonsList.add(button1);
        buttonsList.add(button2);
        WxCpMessage message = WxCpMessage
                .TEMPLATECARD()
                .cardType("button_interaction")
                .toUser(userString)
                .mainTitleTitle("欢迎使用企业微信")
                .mainTitleDesc("您的好友正在邀请您加入企业微信")
                .horizontalContents(content)
                .buttons(buttonsList)
                .build();
        WxCpMessageSendResult messageSendResult = wxService.getMessageService().send(message);
        log.info("发送结果:{}", messageSendResult);

    }

}
