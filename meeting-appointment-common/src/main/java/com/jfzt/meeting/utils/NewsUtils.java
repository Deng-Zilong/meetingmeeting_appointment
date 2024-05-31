//package com.jfzt.meeting.utils;
//
//import me.chanjar.weixin.common.error.WxErrorException;
//import me.chanjar.weixin.cp.api.WxCpService;
//import me.chanjar.weixin.cp.bean.message.WxCpMessage;
//import me.chanjar.weixin.cp.bean.message.WxCpMessageSendResult;
//import org.springframework.beans.factory.annotation.Autowired;
//
//public class NewsUtils {
//
//    @Autowired
//    protected WxCpService wxService;
//
//    public void New() throws WxErrorException {
//        WxCpMessage message = WxCpMessage
//                .MARKDOWN()
//                .toUser("ZhaiChenYu|GuLi|d|QianRuoXiaMo|XingChen|")
//                .content("您的会议室已经预定，稍后会同步到`邮箱` \n" +
//                        "                >**事项详情** \n" +
//                        "                >事　项：开会\n" +
//                        "                >组织者：@miglioguan \n" +
//                        "                >参与者：@miglioguan、@kunliu、@jamdeezhou、@kanexiong、@kisonwang \n" +
//                        "                > \n" +
//                        "                >会议室：广州TIT 1楼 301\n" +
//                        "                >日　期：2018年5月18日\n" +
//                        "                >时　间：上午9:00-11:00\n" +
//                        "                > \n" +
//                        "                >请准时参加会议。 \n" +
//                        "                > \n" +
//                        "                >如需修改会议信息，请点击：[修改会议信息](https://work.weixin.qq.com)")
//                .build();
//
//        WxCpMessageSendResult messageSendResult = this.wxService.getMessageService().send(message);
//    }
//}
