package com.jfzt.meeting.config;

import lombok.Data;
import me.chanjar.weixin.cp.api.impl.WxCpServiceImpl;
import me.chanjar.weixin.cp.config.impl.WxCpDefaultConfigImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * 企业微信配置连接
 *
 * @author zhenxing.lu
 * @since 2024-04-30 10.13:51
 */
@Data
@Configuration
public class WxCpDefaultConfiguration {

    /**
     * 企业微信ID
     */
    @Value("${qiyewx.corpid}")
    private String corpid;

    /**
     * 企业应用的凭证密钥
     */
    @Value("${qiyewx.corpsecret}")
    private String corpsecret;

    /**
     * 软件id
     */
    @Value("${qiyewx.agentId}")
    private Integer agentId;

    /**
     * 开发者设置的token
     */
    @Value("${qiyewx.token}")
    private String token;

    /**
     * 开发者设置的EncodingAESKey
     */
    @Value("${qiyewx.encodingAESKey}")
    private String encodingAESKey;

    @Bean
    public WxCpServiceImpl wxCp() {
        WxCpDefaultConfigImpl config = new WxCpDefaultConfigImpl();
        // 设置微信企业号的appid
        config.setCorpId(corpid);
        // 设置微信企业号的app corpSecret
        config.setCorpSecret(corpsecret);
        config.setAgentId(agentId);
        WxCpServiceImpl wxCpService = new WxCpServiceImpl();
        wxCpService.setWxCpConfigStorage(config);
        return wxCpService;
    }



}
