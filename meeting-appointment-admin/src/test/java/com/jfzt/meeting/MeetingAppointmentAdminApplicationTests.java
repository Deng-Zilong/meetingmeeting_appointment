package com.jfzt.meeting;




import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class MeetingAppointmentAdminApplicationTests {

    @Test
    void contextLoads() {
        String jsonStr = "{'head':{'version':'1'},'dataList':{'resCode':'ss','list':[{'name':'file1','type':'0'},{'name':'file2','type':'1'}]}}";
        JSONObject dataJson= JSONObject.fromObject(jsonStr);
        JSONObject  dataList=dataJson.getJSONObject("dataList");
        JSONArray list=dataList.getJSONArray("list");
        JSONObject info=list.getJSONObject(1);
        String name=info.getString("name");
        String type=info.getString("type");
        System.out.println(name+"\n"+type);
    }

}
