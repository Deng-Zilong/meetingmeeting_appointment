package com.meeting_appointment_back.controller;

import com.meeting_appointment_back.common.utils.weixin.mp.aes.WXBizJsonMsgCrypt;
import com.meeting_appointment_back.service.MeetingGroupService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/callback")
public class test {
    @Autowired
    private MeetingGroupService meetingGroupService;
//
//    @GetMapping("test")
//    public Result test1() {
//        List<MeetingGroupEntity> groupManagementEntities = meetingGroupService.find();
//
//        return Result.ok((Map<String, Object>) groupManagementEntities);
//    }

    @GetMapping("/data")
    public void test1(@RequestParam("msg_signature") String msg_signature,
                      @RequestParam("nonce") String nonce,
                      @RequestParam("timestamp") String timestamp,
                      @RequestParam("echostr") String echostr,
                      HttpServletResponse response
                      ) {

        String sToken = "VWrPCnD67Q";
        String sCorpID = "ww7584980685ae6516";
        String sEncodingAESKey = "BaJPFRGhK7BH5lNm4DzhwsbjDY4PSbQAMmGGhAypyb6";
        String sEchoStr = "";
        try {
            WXBizJsonMsgCrypt wxcpt = new WXBizJsonMsgCrypt(sToken, sEncodingAESKey, sCorpID);
            sEchoStr = wxcpt.VerifyURL(msg_signature,timestamp,nonce,echostr);
            response.getWriter().print(sEchoStr);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
