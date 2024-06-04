package com.jfzt.meeting.config;

import com.alibaba.fastjson.JSONObject;
import com.jfzt.meeting.properties.JwtProperties;
import com.jfzt.meeting.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

/**
 * 拦截判断是否有token
 *
 * @author zhenxing.lu
 * @since 2024-04-30 10.13:51
 */
@Slf4j
@Component
public class TokenInterceptor implements HandlerInterceptor {

    @Resource
    private JwtProperties jwtProperties;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;


    @Override
    public boolean preHandle (@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                              @NonNull Object handler) throws IOException {
        //判断当前拦截到的是Controller的方法还是其他资源
        if (!(handler instanceof HandlerMethod)) {
            //当前拦截到的不是动态方法，直接放行
            return true;
        }
        //1、从请求头中获取令牌
        String token = request.getHeader(jwtProperties.getUserTokenName());
        String accessToken = "accessToken";
        //2、校验令牌
        try {
            log.info("jwt校验:{}", token);
            Claims claims = JwtUtil.parseJWT(jwtProperties.getAdminSecretKey(), token);
            JSONObject jsonObject = JSONObject.parseObject((String)
                    redisTemplate.opsForValue().get("userInfo:" + claims.get("sysUserId")));
            Boolean flag = jsonObject.get(accessToken).equals(token);
            return flag;
            //3、通过，放行
        } catch (Exception ex) {
            //4、不通过，响应401状态码
            response.setStatus(402);
            return false;
        }
    }
}