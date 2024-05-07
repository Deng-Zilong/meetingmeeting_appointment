package com.jfzt.meeting.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 解决跨域的配置
 *
 * @author zhenxing.lu
 * @since 2024-04-30 10.13:51
 */

@Configuration
public class MeetingCorsConfiguration implements WebMvcConfigurer {
    /**
     * 全局CORS配置
     *
     * @param registry CORS注册器
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 映射服务器中那些http接口进行跨域访问
        registry.addMapping("/cors/*")
                // 配置哪些来源有权限跨域
                .allowedOrigins("*")
                // 配置运行跨域访问的请求方法
                .allowedMethods("GET", "POST", "DELETE", "PUT");
    }
}