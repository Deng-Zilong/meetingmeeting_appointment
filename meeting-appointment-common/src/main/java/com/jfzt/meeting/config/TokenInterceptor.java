package com.jfzt.meeting.config;

import com.jfzt.meeting.exception.ErrorCodeEnum;
import com.jfzt.meeting.properties.JwtProperties;
import com.jfzt.meeting.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

/**
 * 拦截判断是否有token
 * @author zhenxing.lu
 * @since 2024-04-30 10.13:51
 */
@Slf4j
@Component
public class  TokenInterceptor  implements HandlerInterceptor {

    @Resource
    private JwtProperties jwtProperties;

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                                                  @NonNull Object handler) throws IOException {
        response.addHeader("Access-Control-Expose-Headers","token");
        //判断当前拦截到的是Controller的方法还是其他资源
        if (!(handler instanceof HandlerMethod)) {
            //当前拦截到的不是动态方法，直接放行
            return true;
        }
        //放行OPTIONS请求
//        if (HttpMethod.GET.toString().equals(request.getMethod())) {
//            System.out.println("OPTIONS请求，放行");
//            return true;
//        }
        //1、从请求头中获取令牌
        String token = request.getHeader(jwtProperties.getUserTokenName());

        //2、校验令牌
        try {
            log.info("jwt校验:{}", token);
            Claims claims = JwtUtil.parseJWT(jwtProperties.getAdminSecretKey(), token);
            //3、通过，放行
            return true;
        } catch (Exception ex) {
            //4、不通过，响应401状态码
            response.setStatus(401);
            return false;
        }
    }
}