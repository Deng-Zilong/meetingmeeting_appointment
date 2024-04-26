package com.jfzt.meeting.controller;

import com.jfzt.meeting.service.AttendeesService;
import com.jfzt.meeting.utils.weixin.mp.aes.WXBizJsonMsgCrypt;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zilong.deng
 * @date 2024/04/26
 */
@RestController
@RequestMapping("/callback")
public class test {
    @Autowired
    AttendeesService attendeesService;


    @GetMapping("/data")
    public void test1 (@RequestParam("msg_signature") String msgSignature,
                       @RequestParam("nonce") String nonce,
                       @RequestParam("timestamp") String timestamp,
                       @RequestParam("echostr") String echostr,
                       HttpServletResponse response
    ) {

        String sToken = "VWrPCnD67Q";
        String sCorpID = "ww7584980685ae6516";
        String sEncodingAESKey = "BaJPFRGhK7BH5lNm4DzhwsbjDY4PSbQAMmGGhAypyb6";
        String sEchoStr;
        try {
            WXBizJsonMsgCrypt wxcpt = new WXBizJsonMsgCrypt(sToken, sEncodingAESKey, sCorpID);
            sEchoStr = wxcpt.VerifyURL(msgSignature, timestamp, nonce, echostr);
            response.getWriter().print(sEchoStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
