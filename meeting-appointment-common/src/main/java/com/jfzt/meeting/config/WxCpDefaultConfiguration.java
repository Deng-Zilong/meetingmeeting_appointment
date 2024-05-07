package com.jfzt.meeting.config;

import me.chanjar.weixin.cp.api.impl.WxCpServiceImpl;
import me.chanjar.weixin.cp.config.impl.WxCpDefaultConfigImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * 企业微信配置连接
 *
 * @author zilong.deng
 * @since 2024-04-30 10.13:51
 */

@Configuration
public class WxCpDefaultConfiguration {

    @Bean
    public WxCpServiceImpl wxCp() {
        WxCpDefaultConfigImpl config = new WxCpDefaultConfigImpl();
        // 设置微信企业号的appid
        config.setCorpId("ww942086e6c44abc4b");
        // 设置微信企业号的app corpSecret
        config.setCorpSecret("Rnf3LVxbAdTfvGVTirgwVbgsaDoBv_MTXrmawAu9qHQ");
        // 设置微信企业号应用ID
        config.setAgentId(1000002);
        WxCpServiceImpl wxCpService = new WxCpServiceImpl();
        wxCpService.setWxCpConfigStorage(config);
        return wxCpService;
    }

}
