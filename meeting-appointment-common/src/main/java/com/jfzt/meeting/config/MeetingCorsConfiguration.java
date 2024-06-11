package com.jfzt.meeting.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 跨域处理，拦截器
 * @author zhenxing.lu
 * @since 2024-04-30 10:13:51
 */

@Configuration
public class MeetingCorsConfiguration implements WebMvcConfigurer {

    @Override
    public void addCorsMappings (CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    @Autowired
    private TokenInterceptor tokenInterceptor;

//    @Override
//    public void addInterceptors (InterceptorRegistry registry) {
//        // 添加拦截器，并设置拦截的路径
//        registry.addInterceptor(tokenInterceptor)
//                // 拦截所有路径
//                .addPathPatterns("/**")
//                // 排除登录和错误处理路径
//                .excludePathPatterns("/meeting/user/info",
//                        "/meeting/user/captcha.jpg",
//                        "/meeting/user/login",
//                        "/quartz/receiving-users",
//                        "/quartz/test",
//                        "/meeting/user/qr-code",
//                        "/meeting/user/oauth2/authorize",
//                        "meeting/user/delete");
//    }


}