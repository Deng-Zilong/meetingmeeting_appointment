package com.jfzt.meeting.utils.xml;


import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.util.XmlUtils;
import me.chanjar.weixin.common.util.xml.XStreamCDataConverter;

import java.io.Serializable;
import java.util.Map;

/**
 * @author zhenxing.lu
 * @date 2024/04/25
 */
@XStreamAlias("xml")
@Data
@Slf4j
@ToString(includeFieldNames = true)
public class CallBackParam implements Serializable {


    /**
     * 使用dom4j解析的存放所有xml属性和值的map.
     */
    private Map<String, Object> allFieldsMap;

    @XStreamAlias("ToUserName")
    @XStreamConverter(value = XStreamCDataConverter.class)
    protected String toUserName;

    @XStreamAlias("AgentID")
    @XStreamConverter(value = XStreamCDataConverter.class)
    protected String agentId;

    @XStreamAlias("Encrypt")
    @XStreamConverter(value = XStreamCDataConverter.class)
    protected String encrypt;

    public static CallBackParam fromXml(String xml) {
        //修改微信变态的消息内容格式，方便解析
        final CallBackParam xmlPackage = XStreamTransformer.fromXml(CallBackParam.class, xml);
        xmlPackage.setAllFieldsMap(XmlUtils.xml2Map(xml));
        log.info("xmlPackage"+xmlPackage);
        return xmlPackage;
    }
}
